package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.applyDTO.AcceptRequest;
import mementee.mementee.api.domain.Apply;
import mementee.mementee.api.domain.Board;
import mementee.mementee.api.domain.Matching;
import mementee.mementee.api.domain.Member;
import mementee.mementee.api.domain.enumtype.BoardType;
import mementee.mementee.api.repository.MatchingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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


    //수락 기능----------
    @Transactional
    public void saveMatching(AcceptRequest request, Long applyId, String authorizationHeader) {
        Board board = boardService.findBoard(request.getBoardId());
        Apply apply = applyService.findApplication(applyId);

        LocalDate consultDate = apply.getDate();                        //신청할 때 등록한 날짜
        LocalTime consultTime = apply.getStartTime();                   //신청할 때 등록한 시작 시간

        Long receiveMemberId = apply.getReceiveMember().getId();        //신청 받을(게시물 글쓴이) 사람의 아이디

        memberService.isCheckMe(authorizationHeader, receiveMemberId);

        if (apply.getBoard().getBoardType() == BoardType.MENTOR){ //신청을 수락하는 메소드이기 때문에 글에 대한 타입에 따라 멘토, 멘티 역할
            Member mentor = apply.getReceiveMember();
            Member mentee = apply.getSendMember();

            Matching matching = new Matching(consultDate, consultTime, board.getConsultTime(),
                    board, apply, mentor, mentee);

            board.getMatchings().add(matching);
            mentor.getMyMenteeMatching().add(matching);
            mentee.getMyMentorMatching().add(matching);

            matchingRepository.saveMatch(matching);
        }else {
            Member mentor = apply.getSendMember();
            Member mentee = apply.getReceiveMember();

            Matching matching = new Matching(consultDate, consultTime, board.getConsultTime(),
                    board, apply, mentor, mentee);

            board.getMatchings().add(matching);
            mentor.getMyMenteeMatching().add(matching);
            mentee.getMyMentorMatching().add(matching);

            matchingRepository.saveMatch(matching);
        }

        board.addUnavailableTimes(consultDate, consultTime);
        apply.updateState();
    }

    //거절-----
    @Transactional
    public void declineMatching(Long applyId){
        Apply apply = applyService.findApplication(applyId);
        apply.updateState();
    }

    public Matching findMatching(Long matchingId){
        return matchingRepository.findMatching(matchingId);
    }

    //멘토/멘티로 매칭 목록
    public List<Matching> findMyMatching(BoardType boardType, String authorizationHeader){
        Member member = memberService.getMemberByToken(authorizationHeader);
        return matchingRepository.findMatching(boardType, member.getId());
    }

}
