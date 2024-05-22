package com.mementee.api.service;

import com.mementee.api.domain.*;
import com.mementee.api.dto.boardDTO.BoardDTO;
import com.mementee.api.dto.boardDTO.BoardImageDTO;
import com.mementee.api.dto.boardDTO.BoardInfoResponse;
import com.mementee.api.dto.subBoardDTO.*;
import com.mementee.api.repository.subBoard.ReplyRepository;
import com.mementee.api.repository.subBoard.SubBoardImageRepository;
import com.mementee.api.repository.subBoard.SubBoardLikeRepository;
import com.mementee.api.repository.subBoard.SubBoardRepository;
import com.mementee.api.validation.BoardValidation;
import com.mementee.api.validation.SubBoardValidation;
import com.mementee.exception.notFound.BoardNotFound;
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

    //모든 게시글 목록
    public Page<SubBoard> findAllByBoardTypeByPage(Pageable pageable){
        return subBoardRepository.findAll(pageable);
    }

    //Member 와 Board 에 대한 좋아요 Entity 조회
    public Optional<SubBoardLike> findSubBoardLikeByMemberAndBoard(Member member, SubBoard subBoard){
        return subBoardLikeRepository.findSubBoardLikeByMemberAndSubBoard(member, subBoard);
    }

    //게시글에 속한 댓글 목록
    public Page<Reply> findAllReply(Long subBoardId, Pageable pageable){
        SubBoard subBoard = findSubBoardById(subBoardId);
        return replyRepository.findRepliesBySubBoard(subBoard, pageable);
    }

    //게시글에 속한 이미지 저장
    @Transactional
    public List<SubBoardImage> saveSubBoardImageUrl(List<MultipartFile> multipartFiles) {
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
        Member member = memberService.findMemberByToken(authorizationHeader);
        List<SubBoardImage> subBoardImages = saveSubBoardImageUrl(multipartFiles);
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

    //좋아요 누르기
    @Transactional
    public void addSubBoardLike(Long subBoardId, String authorizationHeader){
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = findSubBoardById(subBoardId);
        SubBoardValidation.isCheckAddSubBordLike(findSubBoardLikeByMemberAndBoard(member, subBoard));
        SubBoardLike subBoardLike = new SubBoardLike(member, subBoard);
        member.addSubBoardLike(subBoardLike);
        subBoardLikeRepository.save(subBoardLike);
    }

    //좋아요 취소
    @Transactional
    public void removeSubBoardLike(Long subBoardId, String authorizationHeader){
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = findSubBoardById(subBoardId);
        SubBoardLike subBoardLike = SubBoardValidation.isCheckRemoveSubBoardLike(findSubBoardLikeByMemberAndBoard(member,subBoard));
        member.removeSubeBoardLike(subBoardLike);
        subBoardLikeRepository.delete(subBoardLike);
    }


    //댓글 등록
    @Transactional
    public void saveReply(ReplyRequest request, Long subBoardId, String authorizationHeader){
        Member member = memberService.findMemberByToken(authorizationHeader);
        SubBoard subBoard = findSubBoardById(subBoardId);
        Reply reply = new Reply(request.getContent(), member, subBoard);
        replyRepository.save(reply);
    }

}
