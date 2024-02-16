package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.applyDTO.AcceptRequest;
import mementee.mementee.api.domain.Apply;
import mementee.mementee.api.domain.Board;
import mementee.mementee.api.domain.Matching;
import mementee.mementee.api.domain.Member;
import mementee.mementee.api.domain.enumtype.ApplyState;
import mementee.mementee.api.domain.enumtype.BoardType;
import mementee.mementee.api.repository.MatchingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingRepository matchingRepository;

    private final BoardService boardService;
    private final MemberService memberService;
    private final ApplyService applyService;

    public void isCheckCompleteApply(Apply apply){
        if(apply.getApplyState() == ApplyState.COMPLETE)
            throw new IllegalArgumentException("이미 완료된 신청입니다.");
    }

    //수락 기능----------
    @Transactional
    public void saveMatching(AcceptRequest request, Long applyId, String authorizationHeader) {
        Board board = boardService.findBoard(request.getBoardId());
        Apply apply = applyService.findApplication(applyId);

        isCheckCompleteApply(apply);

        LocalDate consultDate = apply.getDate();                        //신청할 때 등록한 날짜
        LocalTime consultTime = apply.getStartTime();                   //신청할 때 등록한 시작 시간

        Long receiveMemberId = apply.getReceiveMember().getId();        //신청 받을(게시물 글쓴이) 사람의 Id
        memberService.isCheckMe(authorizationHeader, receiveMemberId);

        Member mentor;
        Member mentee;

        if (apply.getBoard().getBoardType() == BoardType.MENTOR) { //신청을 수락하는 메소드이기 때문에 글에 대한 타입에 따라 멘토, 멘티 역할
            mentor = apply.getReceiveMember();
            mentee = apply.getSendMember();
        } else {
            mentor = apply.getSendMember();
            mentee = apply.getReceiveMember();
        }

        Matching matching = new Matching(consultDate, consultTime, board.getConsultTime(),
                board, apply, mentor, mentee);

        board.getMatchings().add(matching);
        mentor.getMyMenteeMatching().add(matching);
        mentee.getMyMentorMatching().add(matching);

        board.addUnavailableTimes(consultDate, consultTime);
        apply.updateState();

        matchingRepository.saveMatch(matching);
    }

    //거절-----
    @Transactional
    public void declineMatching(Long applyId, String authorizationHeader){
        Apply apply = applyService.findApplication(applyId);

        Long receiveMemberId = apply.getReceiveMember().getId();        //신청 받을(게시물 글쓴이) 사람의 Id
        memberService.isCheckMe(authorizationHeader, receiveMemberId);

        apply.updateState();
    }

    public Matching findMatching(Long matchingId){
        return matchingRepository.findMatching(matchingId);
    }

    //멘토/멘티로 매칭 목록
    public List<Matching> findMyMatching(BoardType boardType, String authorizationHeader){
        Member member = memberService.getMemberByToken(authorizationHeader);
        return matchingRepository.findMatching(boardType, member);
    }

}
