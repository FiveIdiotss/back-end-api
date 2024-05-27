package com.mementee.api.service;

import com.mementee.api.domain.*;
import com.mementee.api.domain.enumtype.SubBoardType;
import com.mementee.api.dto.subBoardDTO.*;
import com.mementee.api.repository.subBoard.ReplyRepository;
import com.mementee.api.repository.subBoard.SubBoardImageRepository;
import com.mementee.api.repository.subBoard.SubBoardLikeRepository;
import com.mementee.api.repository.subBoard.SubBoardRepository;
import com.mementee.api.validation.MemberValidation;
import com.mementee.api.validation.SubBoardValidation;
import com.mementee.exception.notFound.BoardNotFound;
import com.mementee.exception.notFound.ReplyNotFound;
import com.mementee.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    //게시글 조회시 필요한 Info
    public SubBoardInfoResponse createSubBoardInfoResponse(Long subBoardId, String authorizationHeader){
        SubBoard subBoard = findSubBoardById(subBoardId);
        List<SubBoardImageDTO> subBoardImagesDTOS = SubBoardImageDTO.createSubBoardImageDTOs(findSubBoardImagesBySubBoard(subBoard));
        SubBoardDTO subBoardDTO = createSubBoardDTO(subBoard, authorizationHeader);
        return new SubBoardInfoResponse(subBoardDTO, subBoardImagesDTOS);
    }

    //게시글 조회시 필요한 DTO
    public SubBoardDTO createSubBoardDTO(SubBoard subBoard, String authorizationHeader){
        if(authorizationHeader == null)
            return SubBoardDTO.createSubBoardDTO(subBoard, false);
        Member member = memberService.findMemberByToken(authorizationHeader);
        return SubBoardDTO.createSubBoardDTO(subBoard, SubBoardValidation.isSubBoardLike(findSubBoardLikeByMemberAndBoard(member, subBoard)));
    }

    //게시글 목록시 필요한 DTO List
    public List<SubBoardDTO> createSubBoardDTOs(List<SubBoard> subBoards, String authorizationHeader){
        if(authorizationHeader == null)
            return SubBoardDTO.createSubBoardDTOs(subBoards, false);
        Member member = memberService.findMemberByToken(authorizationHeader);
        return subBoards.stream()
                .map(b -> SubBoardDTO.createSubBoardDTO(b, SubBoardValidation.isSubBoardLike(findSubBoardLikeByMemberAndBoard(member, b))))
                .toList();
    }

    //id로 게시글 조회
    public SubBoard findSubBoardById(Long subBoardId){
        Optional<SubBoard> subBoard = subBoardRepository.findById(subBoardId);
        if(subBoard.isEmpty())
            throw new BoardNotFound();
        return subBoard.get();
    }

    //게시글에 속한 이미지 목록
    public List<SubBoardImage> findSubBoardImagesBySubBoard(SubBoard subBoard){
        return subBoardImageRepository.findSubBoardImageBySubBoard(subBoard);
    }

    //모든 자유 게시글 목록
    public Page<SubBoard> findAllFreeSubBoard(Pageable pageable){
        return subBoardRepository.findAllBySubBoardType(SubBoardType.FREE, pageable);
    }

    //모든 요청 게시글 목록
    public Page<SubBoard> findAllRequestSubBoard(Pageable pageable){
        return subBoardRepository.findAllBySubBoardType(SubBoardType.REQUEST, pageable);
    }

    //Member 와 Board 에 대한 좋아요 Entity 조회
    public Optional<SubBoardLike> findSubBoardLikeByMemberAndBoard(Member member, SubBoard subBoard){
        return subBoardLikeRepository.findSubBoardLikeByMemberAndSubBoard(member, subBoard);
    }

    //게시글 조회
    public Reply findReplyById(Long replyId){
        Optional<Reply> reply = replyRepository.findById(replyId);
        if(reply.isEmpty())
            throw new ReplyNotFound();
        return reply.get();
    }

    //게시글에 속한 댓글 목록
    public Page<Reply> findAllReply(Long subBoardId, Pageable pageable){
        SubBoard subBoard = findSubBoardById(subBoardId);
        return replyRepository.findRepliesBySubBoard(subBoard, pageable);
    }

    //게시글에 속한 이미지 저장
    public void saveSubBoardImageUrl(List<MultipartFile> multipartFiles, SubBoard subBoard) {
        if(multipartFiles == null) return;
        for(MultipartFile multipartFile : multipartFiles){
            String url = s3Service.saveFile(multipartFile);
            SubBoardImage boardImage = new SubBoardImage(url, subBoard);
            subBoardImageRepository.save(boardImage);
        }
    }

    //게시글에 속한 이미지 수정
    public void modifySubBoardImage(List<MultipartFile> multipartFiles, SubBoard subBoard){
        if(multipartFiles == null) return;
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

    //자유 게시글 등록
    @Transactional
    public void saveFreeSubBoard(WriteSubBoardRequest request, String authorizationHeader, List<MultipartFile> multipartFiles){
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = new SubBoard(request.getTitle(), request.getContent(), member, request.getBoardCategory(),SubBoardType.FREE);
        saveSubBoardImageUrl(multipartFiles, subBoard);
        subBoardRepository.save(subBoard);
    }

    @Transactional
    public void saveRequestSubBoard(WriteSubBoardRequest request, String authorizationHeader, List<MultipartFile> multipartFiles){
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = new SubBoard(request.getTitle(), request.getContent(), member, request.getBoardCategory(),SubBoardType.REQUEST);
        saveSubBoardImageUrl(multipartFiles, subBoard);
        subBoardRepository.save(subBoard);
    }

    //게시물 수정
    @Transactional
    public void modifySubBoard(WriteSubBoardRequest request, String authorizationHeader, List<MultipartFile> updatedImages, Long subBoardId){
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = findSubBoardById(subBoardId);
        MemberValidation.isCheckMe(member, subBoard.getMember());
        modifySubBoardImage(updatedImages, subBoard);
        subBoard.modifySubBoard(request);
    }


    //좋아요 누르기
    @Transactional
    public void addSubBoardLike(Long subBoardId, String authorizationHeader){
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = findSubBoardById(subBoardId);
        SubBoardValidation.isCheckAddSubBordLike(findSubBoardLikeByMemberAndBoard(member, subBoard));
        SubBoardLike subBoardLike = new SubBoardLike(member, subBoard);
        subBoard.plusLikeCount();
        subBoardLikeRepository.save(subBoardLike);
    }

    //좋아요 취소
    @Transactional
    public void removeSubBoardLike(Long subBoardId, String authorizationHeader){
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = findSubBoardById(subBoardId);
        SubBoardLike subBoardLike = SubBoardValidation.isCheckRemoveSubBoardLike(findSubBoardLikeByMemberAndBoard(member,subBoard));
        subBoard.minusLikeCount();
        subBoardLikeRepository.delete(subBoardLike);
    }


    //댓글 수정
    @Transactional
    public void modifyReply(ReplyRequest request, Long replyId, String authorizationHeader){
        Member member = memberService.findMemberByToken(authorizationHeader);
        Reply reply = findReplyById(replyId);
        MemberValidation.isCheckMe(member, reply.getMember());
        reply.modifyReply(request);
    }

    //댓글 등록
    @Transactional
    public void saveReply(ReplyRequest request, Long subBoardId, String authorizationHeader){
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = findSubBoardById(subBoardId);
        Reply reply = new Reply(request.getContent(), member, subBoard);
        subBoard.plusReplyCount();
        replyRepository.save(reply);
    }

    //댓글 삭제
    @Transactional
    public void removeReply(Long replyId, String authorizationHeader){
        Member member = memberService.findMemberByToken(authorizationHeader);
        Reply reply = findReplyById(replyId);
        MemberValidation.isCheckMe(member, reply.getMember());
        reply.getSubBoard().minusReplyCount();
        replyRepository.delete(reply);
    }
}
