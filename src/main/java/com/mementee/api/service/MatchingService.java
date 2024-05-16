package com.mementee.api.service;

import com.mementee.api.domain.Apply;
import com.mementee.api.domain.Board;
import com.mementee.api.domain.Matching;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.domain.enumtype.ApplyState;
import com.mementee.api.repository.MatchingRepository;
import com.mementee.api.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.enumtype.BoardType;
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
    private final ChatRoomRepository chatRoomRepository;

    private final MemberService memberService;
    private final ApplyService applyService;

    private void isCheckCompleteApply(Apply apply){
        if(apply.getApplyState() == ApplyState.COMPLETE)
            throw new IllegalArgumentException("이미 완료된 신청입니다.");
    }

    //수락 기능----------
    @Transactional
    public void saveMatching(Long applyId, String authorizationHeader) {
        Apply apply = applyService.findApplication(applyId);
        Board board = apply.getBoard();

        isCheckCompleteApply(apply);

        LocalDate consultDate = apply.getDate();                        //신청할 때 등록한 날짜
        LocalTime consultTime = apply.getStartTime();                   //신청할 때 등록한 시작 시간

        Long receiveMemberId = apply.getReceiveMember().getId();        //신청 받을(게시물 글쓴이) 사람의 Id
        memberService.isCheckMe(authorizationHeader, receiveMemberId);

        Member mentor = apply.getReceiveMember();;
        Member mentee = apply.getSendMember();

        Matching matching = new Matching(consultDate, consultTime, board.getConsultTime(),
                board, apply, mentor, mentee);

        ChatRoom chatRoom = new ChatRoom(mentor, mentee, matching);

        board.getMatchings().add(matching);
        mentor.getMyMenteeMatching().add(matching);
        mentee.getMyMentorMatching().add(matching);

        board.addUnavailableTimes(consultDate, consultTime);
        apply.updateState();

        matchingRepository.saveMatch(matching);
        chatRoomRepository.save(chatRoom);
    }

    //거절-----
    @Transactional
    public void declineMatching(Long applyId, String authorizationHeader){
        Apply apply = applyService.findApplication(applyId);

        isCheckCompleteApply(apply);

        Long receiveMemberId = apply.getReceiveMember().getId();        //신청 받을(게시물 글쓴이) 사람의 Id
        memberService.isCheckMe(authorizationHeader, receiveMemberId);

        apply.updateState();
    }

    public Matching findMatching(Long matchingId){
        return matchingRepository.findMatching(matchingId);
    }

    //멘토/멘티 매칭 목록
    public List<Matching> findMyMatching(BoardType boardType, Long memberId){
        if(boardType == BoardType.MENTOR){
            return findMyMentor(memberId);
        }
        return findMyMentee(memberId);
    }

    public List<Matching> findMyMentor(Long memberId){
        return matchingRepository.findMyMentor(memberId);
    }

    public List<Matching> findMyMentee(Long memberId){
        return matchingRepository.findMyMentee(memberId);
    }

}
