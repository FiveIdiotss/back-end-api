package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.applicationDTO.ApplicationRequest;
import mementee.mementee.api.domain.*;
import mementee.mementee.api.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final MemberService memberService;
    private final BoardService boardService;

    //멘토 or 멘티 신청 하기
    @Transactional
    public void sendApply(String authorizationHeader, Long boardId, ApplicationRequest request){
        Board board = boardService.findBoard(boardId);

        Member sendMember = memberService.getMemberByToken(authorizationHeader);
        Member receiveMember = board.getMember();

        isCheckMyBoard(sendMember,board);
        isDuplicateApply(sendMember.getId(), receiveMember.getId(), board.getId());
        Application application = new Application(request.getDate(), request.getTime(), sendMember, receiveMember, board, request.getContent());

        sendMember.getSendApplications().add(application);
        board.getMember().getReceiveApplications().add(application);
        board.getApplications().add(application);

        applicationRepository.saveApplication(application);
    }

    public Application findApplication(Long applicationId){
        return applicationRepository.findApplication(applicationId);
    }

    //신청 중복 체크
    public void isDuplicateApply(Long sendMemberId, Long receiveMemberId, Long boardId){
        Optional<Application> duplicateApply = applicationRepository.isDuplicateApply(sendMemberId, receiveMemberId, boardId);
        if(duplicateApply.isPresent()){
            throw new IllegalArgumentException("이미 신청한 글 입니다.");
        }
    }

    //자신의 글에 신청 체크
    public void isCheckMyBoard(Member member ,Board board){
        if(member == board.getMember())
            throw new IllegalArgumentException("자신의 글에는 신청할 수 없습니다.");
    }

    //자신이 신청한 지원글을 조회 할때
    public Application isCheckMyApplication(String authorizationHeader, Long applyId){
        Member member = memberService.getMemberByToken(authorizationHeader);
        Application application = applicationRepository.findApplication(applyId);
        if(member != application.getSendMember() || member != application.getReceiveMember())
            throw new IllegalArgumentException("나에게 해당하는 지원 글이 아닙니다.");
        return application;
    }

    public List<Application> findApplicationBySendMember(Long memberId){
        return applicationRepository.findApplicationBySendMember(memberId);
    }

    public List<Application> findApplicationByReceiveMember(Long memberId){
        return applicationRepository.findApplicationByReceiveMember(memberId);
    }
}
