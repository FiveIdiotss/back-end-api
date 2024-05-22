package com.mementee.api.service;

import com.mementee.api.domain.Apply;
import com.mementee.api.domain.Board;
import com.mementee.api.domain.Member;
import com.mementee.api.dto.applyDTO.ApplyInfoResponse;
import com.mementee.api.repository.ApplyRepository;
import com.mementee.api.validation.ApplyValidation;
import com.mementee.exception.ForbiddenException;
import com.mementee.exception.conflict.ApplyConflictException;
import com.mementee.exception.conflict.MyApplyConflictException;
import com.mementee.exception.notFound.ApplyNotFound;
import lombok.RequiredArgsConstructor;
import com.mementee.api.dto.applyDTO.ApplyRequest;
import com.mementee.api.domain.enumtype.SendReceive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplyService {

    private final ApplyRepository applicationRepository;
    private final MemberService memberService;
    private final BoardService boardService;

    //하나의 게시물에 대한 신청 조회
    public Optional<Apply> findApplyBySendMemberAndReceiveMemberAndBoard(Member sendMember, Member receiveMember, Board board){
        return applicationRepository.findApplyBySendMemberAndReceiveMemberAndBoard(sendMember, receiveMember, board);
    }

    //나의 신청 목록
    public List<Apply> findMyApply(Member member, SendReceive sendReceive){
        if(sendReceive.equals(SendReceive.SEND))
            return applicationRepository.findAppliesBySendMember(member);
        return applicationRepository.findAppliesByReceiveMember(member);
    }

    //나의 신청 목록(Page)
    public Page<Apply> findMyApplyByPage(Member member, SendReceive sendReceive, Pageable pageable){
        if(sendReceive.equals(SendReceive.SEND))
            return applicationRepository.findAppliesBySendMember(member, pageable);
        return applicationRepository.findAppliesByReceiveMember(member, pageable);
    }

    //id로 신청 조회
    public Apply findApplyById(Long applyId){
        Optional<Apply> apply = applicationRepository.findById(applyId);
        if(apply.isEmpty())
            throw new ApplyNotFound();
        return apply.get();
    }

    //멘토에게 신청하기
    @Transactional
    public void sendApply(String authorizationHeader, Long boardId, ApplyRequest request){
        Board board = boardService.findBoardById(boardId);
        Member sendMember = memberService.findMemberByToken(authorizationHeader);
        Member receiveMember = board.getMember();

        ApplyValidation.isCheckApplyOfMyBoard(sendMember,board);
        ApplyValidation.isCheckDuplicateApply(findApplyBySendMemberAndReceiveMemberAndBoard(sendMember, receiveMember, board));

        Apply apply = new Apply(request.getDate(), request.getTime(), sendMember, receiveMember, board, request.getContent());

        sendMember.getSendApplies().add(apply);
        board.getMember().getReceiveApplies().add(apply);
        board.getApplies().add(apply);

        applicationRepository.save(apply);
    }
}
