package com.team.mementee.api.service;

import com.team.mementee.api.domain.*;
import com.team.mementee.api.domain.enumtype.BoardCategory;
import com.team.mementee.api.domain.enumtype.SubBoardType;
import com.team.mementee.api.dto.subBoardDTO.*;
import com.team.mementee.api.repository.subBoard.ReplyRepository;
import com.team.mementee.api.repository.subBoard.SubBoardImageRepository;
import com.team.mementee.api.repository.subBoard.SubBoardLikeRepository;
import com.team.mementee.api.repository.subBoard.SubBoardRepository;
import com.team.mementee.api.validation.BoardValidation;
import com.team.mementee.api.validation.MemberValidation;
import com.team.mementee.api.validation.SubBoardValidation;
import com.team.mementee.exception.notFound.BoardNotFound;
import com.team.mementee.exception.notFound.ReplyNotFound;
import com.team.mementee.exception.unauthorized.RequiredLoginException;
import com.team.mementee.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubBoardService {

    private final SubBoardRepository subBoardRepository;
    private final ReplyRepository replyRepository;
    private final SubBoardImageRepository subBoardImageRepository;
    private final SubBoardLikeRepository subBoardLikeRepository;

    private final MemberService memberService;
    private final S3Service s3Service;

    public List<SubBoard> findAllByTitleContaining(String query) {
        return subBoardRepository.findAllByTitleContaining(query);
    }

    public List<SubBoard> findAllByContentContaining(String query) {
        return subBoardRepository.findAllByContentContaining(query);
    }

    //게시글 조회시 필요한 Info
    @Transactional
    public SubBoardInfoResponse createSubBoardInfoResponse(Long subBoardId, String authorizationHeader) {
        SubBoard subBoard = findSubBoardById(subBoardId);
        List<SubBoardImageDTO> subBoardImagesDTOS = SubBoardImageDTO.createSubBoardImageDTOs(findSubBoardImagesBySubBoard(subBoard));
        SubBoardDTO subBoardDTO = createSubBoardDTO(subBoard, authorizationHeader);
        return new SubBoardInfoResponse(subBoardDTO, subBoardImagesDTOS);
    }

    //게시글 조회시 필요한 DTO
    public SubBoardDTO createSubBoardDTO(SubBoard subBoard, String authorizationHeader) {
        if (authorizationHeader == null)
            return SubBoardDTO.createSubBoardDTO(subBoard, false, findRepresentImage(subBoard));
        Member member = memberService.findMemberByToken(authorizationHeader);
        return SubBoardDTO.createSubBoardDTO(subBoard, SubBoardValidation.isSubBoardLike(findSubBoardLikeByMemberAndBoard(member, subBoard)), findRepresentImage(subBoard));
    }

    //게시글 목록시 필요한 DTO List
    public List<SubBoardDTO> createSubBoardDTOs(List<SubBoard> subBoards, String authorizationHeader) {
        if (authorizationHeader == null)
            return subBoards.stream()
                    .map(b -> SubBoardDTO.createSubBoardDTO(b, false, findRepresentImage(b)))
                    .toList();

        Member member = memberService.findMemberByToken(authorizationHeader);
        return subBoards.stream()
                .map(b -> SubBoardDTO.createSubBoardDTO(b, SubBoardValidation.isSubBoardLike(findSubBoardLikeByMemberAndBoard(member, b)), findRepresentImage(b)))
                .toList();
    }

    //id로 게시글 조회
    @Transactional
    public SubBoard findSubBoardById(Long subBoardId) {
        Optional<SubBoard> subBoard = subBoardRepository.findByIdWithLock(subBoardId);
        if (subBoard.isEmpty())
            throw new BoardNotFound();
        return subBoard.get();
    }

    //게시글에 속한 이미지 목록
    public List<SubBoardImage> findSubBoardImagesBySubBoard(SubBoard subBoard) {
        return subBoardImageRepository.findSubBoardImageBySubBoard(subBoard);
    }

    //특정 멤버가 쓴 질문/요청 게시글 목록
    public Page<SubBoard> findSubBoardsBySubBoardTypeAndMember(SubBoardType subBoardType, Long memberId, Pageable pageable) {
        Member member = memberService.findMemberById(memberId);
        return subBoardRepository.findSubBoardsBySubBoardTypeAndMember(subBoardType, member, pageable);
    }

    //게시물의 대표 이미지
    public String findRepresentImage(SubBoard subBoard) {
        List<SubBoardImage> images = subBoardImageRepository.findFirstBySubBoardOrderByIdAsc(subBoard);
        if (!images.isEmpty())
            return images.get(0).getSubBoardImageUrl();
        return "";
    }

    //Member 와 Board 에 대한 좋아요 Entity 조회
    public Optional<SubBoardLike> findSubBoardLikeByMemberAndBoard(Member member, SubBoard subBoard) {
        return subBoardLikeRepository.findSubBoardLikeByMemberAndSubBoard(member, subBoard);
    }

    //게시글 조회
    public Reply findReplyById(Long replyId) {
        Optional<Reply> reply = replyRepository.findById(replyId);
        if (reply.isEmpty())
            throw new ReplyNotFound();
        return reply.get();
    }

    //게시글에 속한 댓글 목록
    public Page<Reply> findAllReply(Long subBoardId, Pageable pageable) {
        SubBoard subBoard = findSubBoardById(subBoardId);
        return replyRepository.findRepliesBySubBoard(subBoard, pageable);
    }

    @Cacheable(value = "weeklyTop5Posts", key = "'weeklyTop5Posts:' + #subBoardType.name()")
    public SubBoardDTOs getWeeklyTop5PopularPosts(SubBoardType subBoardType) {
        // 이번 주 시작 (월요일 0시 기준)
        LocalDateTime startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        // 다음 주 시작 (다음 월요일 0시 기준)
        LocalDateTime endDate = startDate.plusWeeks(1);

        // 인기 글 5개 조회
        Pageable top5 = PageRequest.of(0, 5); // 첫 페이지에서 5개
        List<SubBoardDTO> responseDTOs = createSubBoardDTOs(subBoardRepository.findTop5ByLikeCountInLastWeek(startDate, endDate, subBoardType, top5), null);
        return new SubBoardDTOs(responseDTOs);
    }

    //게시글에 속한 이미지 저장
    @Transactional
    public void saveSubBoardImageUrl(List<MultipartFile> multipartFiles, SubBoard subBoard) {
        if (multipartFiles == null) return;
        for (MultipartFile multipartFile : multipartFiles) {
            String url = s3Service.saveFile(multipartFile);
            SubBoardImage boardImage = new SubBoardImage(url, subBoard);
            subBoardImageRepository.save(boardImage);
        }
    }

    //게시글에 속한 이미지 수정
    public void modifySubBoardImage(List<MultipartFile> multipartFiles, SubBoard subBoard) {
        if (multipartFiles == null) return;
        // 기존 이미지 목록
        List<SubBoardImage> existingImages = subBoardImageRepository.findSubBoardImageBySubBoard(subBoard);

        // 새 이미지 URL 목록 생성
        List<String> newImageUrls = new ArrayList<>();
        for (MultipartFile imageFile : multipartFiles) {
            String imageUrl = s3Service.saveFile(imageFile); // S3에 이미지 업로드 후 URL 반환
            newImageUrls.add(imageUrl);
        }

        // 기존 이미지 목록에서 삭제할 이미지 식별
        List<SubBoardImage> imagesToRemove = existingImages.stream()
                .filter(existingImage -> !newImageUrls.contains(existingImage.getSubBoardImageUrl()))
                .toList();

        // 식별된 이미지 삭제
        subBoardImageRepository.deleteAll(imagesToRemove);

        // 새로운 이미지 추가
        for (String url : newImageUrls) {
            if (existingImages.stream().noneMatch(image -> image.getSubBoardImageUrl().equals(url))) {
                SubBoardImage newImage = new SubBoardImage(url, subBoard);
                subBoardImageRepository.save(newImage); // 데이터베이스에 저장
            }
        }
    }

    //글 쓰기
    @Transactional
    public void saveSubBoard(WriteSubBoardRequest request, List<MultipartFile> images, String authorizationHeader) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = new SubBoard(request.getTitle(), request.getContent(), member, request.getBoardCategory(), request.getSubBoardType(), request.getPlatform());
        saveSubBoardImageUrl(images, subBoard);
        subBoardRepository.save(subBoard);
    }

    //게시물 수정
    @Transactional
    @CacheEvict(value = "weeklyTop5Posts", key = "'weeklyTop5Posts'")
    public void modifySubBoard(WriteSubBoardRequest request, String authorizationHeader, Long subBoardId) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = findSubBoardById(subBoardId);
        MemberValidation.isCheckMe(member, subBoard.getMember());
        //modifySubBoardImage(updatedImages, subBoard);
        subBoard.modifySubBoard(request);
    }

    //좋아요 누르기
    @Transactional
    public void addSubBoardLike(Long subBoardId, String authorizationHeader) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = findSubBoardById(subBoardId);
        SubBoardValidation.isCheckAddSubBordLike(findSubBoardLikeByMemberAndBoard(member, subBoard));
        SubBoardLike subBoardLike = new SubBoardLike(member, subBoard);
        subBoard.plusLikeCount();
        subBoardLikeRepository.save(subBoardLike);
    }

    //좋아요 취소
    @Transactional
    public void removeSubBoardLike(Long subBoardId, String authorizationHeader) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = findSubBoardById(subBoardId);
        SubBoardLike subBoardLike = SubBoardValidation.isCheckRemoveSubBoardLike(findSubBoardLikeByMemberAndBoard(member, subBoard));
        subBoard.minusLikeCount();
        subBoardLikeRepository.delete(subBoardLike);
    }

    //댓글 수정
    @Transactional
    public void modifyReply(ReplyRequest request, Long replyId, String authorizationHeader) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        Reply reply = findReplyById(replyId);
        MemberValidation.isCheckMe(member, reply.getMember());
        reply.modifyReply(request);
    }

    //댓글 등록
    @Transactional
    public void saveReply(ReplyRequest request, SubBoard subBoard, Member member) {
        Reply reply = new Reply(request.getContent(), member, subBoard);
        subBoard.plusReplyCount();
        replyRepository.save(reply);
    }

    //댓글 삭제
    @Transactional
    public void removeReply(Long replyId, String authorizationHeader) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        Reply reply = findReplyById(replyId);
        MemberValidation.isCheckMe(member, reply.getMember());
        reply.getSubBoard().minusReplyCount();
        replyRepository.delete(reply);
    }

    public Page<SubBoard> findSubBoardsByFilter(String authorizationHeader,
                                                boolean schoolFilter,
                                                boolean likeFilter,
                                                BoardCategory boardCategory,
                                                String keyWord,
                                                SubBoardType subBoardType,
                                                Pageable pageable) {
        Member member = null;
        School school = null;

        if (schoolFilter || likeFilter) {
            if (authorizationHeader == null)
                throw new RequiredLoginException();
            member = memberService.findMemberByToken(authorizationHeader);
            school = member.getSchool();
        }

        String searchKeyWord = BoardValidation.isContainKeyWord(keyWord);

        //카테고리
        if (boardCategory != null && searchKeyWord == null && !schoolFilter && !likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndBoardCategory(subBoardType, boardCategory, pageable);

        //검색
        if (boardCategory == null && searchKeyWord != null && !schoolFilter && !likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndKeyWord(subBoardType, searchKeyWord, pageable);

        //내 학교
        if (boardCategory == null && searchKeyWord == null && schoolFilter && !likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndMemberSchool(subBoardType, school, pageable);

        //즐겨찾기
        if (boardCategory == null && searchKeyWord == null && !schoolFilter && likeFilter)
            return subBoardRepository.findSubBoardLikesBySubBoardTypeAndMember(subBoardType, member, pageable);

        //카테고리, 검색
        if (boardCategory != null && searchKeyWord != null && !schoolFilter && !likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndBoardCategoryAndKeyWord(subBoardType, boardCategory, searchKeyWord, pageable);

        //카테고리, 내 학교
        if (boardCategory != null && searchKeyWord == null && schoolFilter && !likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndBoardCategoryAndMemberSchool(subBoardType, boardCategory, school, pageable);

        //카테고리, 좋아요
        if (boardCategory != null && searchKeyWord == null && !schoolFilter && likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndBoardCategoryAndSubBoardLike(subBoardType, boardCategory, member, pageable);

        //검색, 내 학교
        if (boardCategory == null && searchKeyWord != null && schoolFilter && !likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndMemberSchoolAndKeyWord(subBoardType, school, searchKeyWord, pageable);

        //검색, 좋아요
        if (boardCategory == null && searchKeyWord != null && !schoolFilter && likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndKeyWordAndSubBoardLike(subBoardType, searchKeyWord, member, pageable);

        //내 학교, 좋아요
        if (boardCategory == null && searchKeyWord == null && schoolFilter && likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndMemberSchoolAndSubBoardLike(subBoardType, school, member, pageable);

        //카테고리, 내 학교, 좋아요
        if (boardCategory != null && searchKeyWord == null && schoolFilter && likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndBoardCategoryAndMemberSchoolAndSubBoardLike(subBoardType, boardCategory, school, member, pageable);

        //카테고리, 검색, 좋아요
        if (boardCategory != null && searchKeyWord != null && !schoolFilter && likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndBoardCategoryAndKeyWordAndSubBoardLike(subBoardType, boardCategory, searchKeyWord, member, pageable);


        //카테고리, 검색, 내 학교
        if (boardCategory != null && searchKeyWord != null && schoolFilter && !likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndBoardCategoryAndMemberSchoolAndKeyWord(subBoardType, boardCategory, school, searchKeyWord, pageable);


        //검색, 내 학교, 좋아요
        if (boardCategory == null && searchKeyWord != null && schoolFilter && likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndMemberSchoolAndKeyWordAndSubBoardLike(subBoardType, school, searchKeyWord, member, pageable);


        //카테고리, 검색, 내 학교, 좋아요
        if (boardCategory != null && searchKeyWord != null && schoolFilter && likeFilter)
            return subBoardRepository.findSubBoardsBySubBoardTypeAndMemberSchoolAndKeyWordAndBoardCategoryAndSubBoardLike(subBoardType, school, searchKeyWord, boardCategory, member, pageable);

        //필터 없는 상태
        return subBoardRepository.findAllBySubBoardType(subBoardType, pageable);
    }
}

