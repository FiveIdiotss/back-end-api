package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.applyDTO.ApplyRequest;
import mementee.mementee.api.domain.*;
import mementee.mementee.api.domain.enumtype.SendReceive;
import mementee.mementee.api.repository.ApplyRepository;
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

    //멘토 or 멘티 신청 하기
    @Transactional
    public void sendApply(String authorizationHeader, Long boardId, ApplyRequest request){
        Board board = boardService.findBoard(boardId);

        Member sendMember = memberService.getMemberByToken(authorizationHeader);
        Member receiveMember = board.getMember();

        isCheckMyBoard(sendMember,board);
        isDuplicateApply(sendMember.getId(), receiveMember.getId(), board.getId());
        Apply apply = new Apply(request.getDate(), request.getTime(), sendMember, receiveMember, board, request.getContent());

        sendMember.getSendApplies().add(apply);
        board.getMember().getReceiveApplies().add(apply);
        board.getApplies().add(apply);

        applicationRepository.saveApplication(apply);
    }

    //신청 중복 체크
    public void isDuplicateApply(Long sendMemberId, Long receiveMemberId, Long boardId){
        Optional<Apply> duplicateApply = applicationRepository.isDuplicateApply(sendMemberId, receiveMemberId, boardId);
        if(duplicateApply.isPresent()){
            throw new IllegalArgumentException("이미 신청한 글 입니다.");
        }
    }

    //자신의 글에 신청 체크
    public void isCheckMyBoard(Member member ,Board board){
        if(member == board.getMember())
            throw new IllegalArgumentException("자신의 글에는 신청할 수 없습니다.");
    }

    //자신이 신청하거나 신청받은 지원글을 조회 할때
    public void isCheckApply(String authorizationHeader, Long applyId){
        Apply apply = applicationRepository.findApplication(applyId);

        Member sendMember = memberService.getMemberByToken(authorizationHeader);            //현재 로그인한 멤버 (신청한 사람)

        if(sendMember != apply.getSendMember() && sendMember != apply.getReceiveMember())
            throw new IllegalArgumentException("나에게 해당하는 지원 글이 아닙니다.");
    }

    //보낸/받은 신청 목록
    public List<Apply> findMyApply(Long memberId, SendReceive sendReceive){
        if(sendReceive.equals(SendReceive.SEND))
            return findApplyBySendMember(memberId);
        return findApplyByReceiveMember(memberId);
    }

    public List<Apply> findApplyBySendMember(Long memberId){
        return applicationRepository.findApplicationBySendMember(memberId);
    }

    public List<Apply> findApplyByReceiveMember(Long memberId){
        return applicationRepository.findApplicationByReceiveMember(memberId);
    }

    public Apply findApplication(Long applicationId){
        return applicationRepository.findApplication(applicationId);
    }
}
