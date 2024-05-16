package com.mementee.api.service;

import com.mementee.api.domain.*;
import com.mementee.api.dto.subBoardDTO.*;
import com.mementee.api.repository.subBoard.ReplyRepository;
import com.mementee.api.repository.subBoard.SubBoardImageRepository;
import com.mementee.api.repository.subBoard.SubBoardLikeRepository;
import com.mementee.api.repository.subBoard.SubBoardRepository;
import com.mementee.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubBoardService {

    private final SubBoardRepository subBoardRepository;
    private final ReplyRepository replyRepository;
    private final SubBoardImageRepository subBoardImageRepository;
    private final SubBoardLikeRepository subBoardLikeRepository;
    private final SchoolService schoolService;

    private final MemberService memberService;
    private final S3Service s3Service;

    //편리 메서드
    public void isCheckBoardMember(Member member, SubBoard subBoard){
        if(!member.equals(subBoard.getMember()))
            throw new IllegalArgumentException("권한이 없습니다.");        //작성자가 아닐경우
    }

    public List<SubBoardDTO> createSubBoardDTO(List<SubBoard> subBoards,  String authorizationHeader){
        if(authorizationHeader == null)
            return subBoards.stream()
                .map(b -> new SubBoardDTO(b.getId(), b.getTitle(), b.getContent(),
                                b.getMember().getYear(), b.getMember().getSchool().getName(), b.getMember().getMajor().getName(),
                                b.getMember().getId(), b.getMember().getMemberImageUrl(), b.getMember().getName(), b.getWriteTime(), false))
                .collect(Collectors.toList());

        Member member = memberService.getMemberByToken(authorizationHeader);
        return subBoards.stream()
                .map(b -> new SubBoardDTO(b.getId(), b.getTitle(), b.getContent(),
                        b.getMember().getYear(), b.getMember().getSchool().getName(), b.getMember().getMajor().getName(),
                        b.getMember().getId(), b.getMember().getMemberImageUrl(), b.getMember().getName(), b.getWriteTime(), isSubBoardLike(member, b)))
                .collect(Collectors.toList());
    }

    public SubBoardInfoResponse createSubBoardInfoResponse(SubBoard subBoard, String authorizationHeader){
        List<SubBoardImage> subBoardImages = getSubBoardImages(subBoard.getId());
        List<SubBoardImageDTO> subBoardImagesDTOS = subBoardImages.stream().
                map(b -> new SubBoardImageDTO(b.getSubBoardImageUrl()))
                .collect(Collectors.toList());

        SubBoardDTO subBoardDTO;
        if(authorizationHeader == null) {
            subBoardDTO = new SubBoardDTO(subBoard.getId(), subBoard.getTitle(), subBoard.getContent(),
                    subBoard.getMember().getYear(), subBoard.getMember().getSchool().getName(), subBoard.getMember().getMajor().getName(),
                    subBoard.getMember().getId(), subBoard.getMember().getMemberImageUrl(), subBoard.getMember().getName(), subBoard.getWriteTime(), false);
        } else {
            Member member = memberService.getMemberByToken(authorizationHeader);
            subBoardDTO = new SubBoardDTO(subBoard.getId(), subBoard.getTitle(), subBoard.getContent(),
                    subBoard.getMember().getYear(), subBoard.getMember().getSchool().getName(), subBoard.getMember().getMajor().getName(),
                    subBoard.getMember().getId(), subBoard.getMember().getMemberImageUrl(), subBoard.getMember().getName(), subBoard.getWriteTime(), isSubBoardLike(member, subBoard));
        }
        return new SubBoardInfoResponse(subBoardDTO, subBoardImagesDTOS);
    }

    //좋아요 기능
    public boolean isSubBoardLike(Member member, SubBoard subBoard){
        Optional<SubBoardLike> subBoardLike = subBoardLikeRepository.findSubBoardLikeByMemberAndSubBoard(member, subBoard);
        return subBoardLike.isPresent();
    }

    public void isCheckSubBordLike(Member member, SubBoard subBoard){
        Optional<SubBoardLike> subBoardLike = subBoardLikeRepository.findSubBoardLikeByMemberAndSubBoard(member, subBoard);
        if(subBoardLike.isPresent())
            throw new IllegalArgumentException("이미 좋아요 한 게시물 입니다.");
    }

    public Optional<SubBoardLike> isCheckMySubBoardLike(Member member, SubBoard subBoard){
        Optional<SubBoardLike> subBoardLike = subBoardLikeRepository.findSubBoardLikeByMemberAndSubBoard(member, subBoard);
        if(subBoardLike.isEmpty())
            throw new IllegalArgumentException("좋아요 항목에 에 존재하지 않는 게시글입니다.");
        return subBoardLike;
    }

    //즐겨찾기 추가
    @Transactional
    public void addSubBoardLike(Long subBoardId, String authorizationHeader){
        Member member = memberService.getMemberByToken(authorizationHeader);
        SubBoard subBoard = subBoardRepository.findOne(subBoardId);

        isCheckSubBordLike(member, subBoard);

        SubBoardLike subBoardLike = new SubBoardLike(member, subBoard);
        member.addSubBoardLike(subBoardLike);

        subBoardLikeRepository.save(subBoardLike);
    }

    @Transactional
    public void removeSubBoardLike(Long subBoardId, String authorizationHeader){
        Member member = memberService.getMemberByToken(authorizationHeader);
        SubBoard subBoard = subBoardRepository.findOne(subBoardId);

        Optional<SubBoardLike> subBoardLike = isCheckMySubBoardLike(member, subBoard);
        SubBoardLike mySubBoardLike = subBoardLikeRepository.findOne(subBoardLike.get().getId());

        member.removeSubeBoardLike(mySubBoardLike);
        subBoardLikeRepository.delete(mySubBoardLike);
    }

    //이미지
    public List<SubBoardImage> getSubBoardImages(Long subBoardId){
        SubBoard subBoard = subBoardRepository.findOne(subBoardId);
        return subBoardImageRepository.findSubBoardImageBySubBoard(subBoard);
    }

    @Transactional
    public List<SubBoardImage> getSubBoardImageUrl(List<MultipartFile> multipartFiles) throws IOException {
        List<SubBoardImage> subBoardImages = new ArrayList<>();
        if (multipartFiles == null) {
            return subBoardImages;
        }
        for(MultipartFile multipartFile : multipartFiles){
            String url = s3Service.saveFile(multipartFile);
            SubBoardImage boardImage = new SubBoardImage(url);
            subBoardImages.add(boardImage);
            subBoardImageRepository.save(boardImage);
        }
        return subBoardImages;
    }

    //게시글 등록
    @Transactional
    public void saveSubBoard(WriteSubBoardRequest request, List<MultipartFile> multipartFiles, String authorizationHeader) throws IOException {
        Member member = memberService.getMemberByToken(authorizationHeader);
        List<SubBoardImage> subBoardImages = getSubBoardImageUrl(multipartFiles);
        SubBoard subBoard;
        if(subBoardImages.isEmpty()){
            subBoard = new SubBoard(request.getTitle(), request.getContent(), member);
        }else {
            subBoard = new SubBoard(request.getTitle(), request.getContent(), member, subBoardImages);
            subBoard.addSubBoardImage(subBoardImages);

            for(SubBoardImage subBoardImage : subBoardImages){
                subBoardImage.setSubBoard(subBoard);
            }
        }
        subBoardRepository.save(subBoard);
    }


    public SubBoard findSubBoard(Long subBoardId){
        return subBoardRepository.findOne(subBoardId);
    }


    //게시글 목록 ---------------------------------
    public Page<SubBoard> findAllByBoardTypeByPage(Pageable pageable){
        return subBoardRepository.findAll(pageable);
    }

    //댓글 ----------------
    @Transactional
    public void saveReply(ReplyRequest request, Long subBoardId, String authorizationHeader){
        Member member = memberService.getMemberByToken(authorizationHeader);
        SubBoard subBoard = findSubBoard(subBoardId);
        Reply reply = new Reply(request.getContent(), member, subBoard);
        replyRepository.save(reply);
    }

    public Page<Reply> findAllReply(Long subBoardId, Pageable pageable){
        SubBoard subBoard = findSubBoard(subBoardId);
        return replyRepository.findRepliesBySubBoard(subBoard, pageable);
    }
}
