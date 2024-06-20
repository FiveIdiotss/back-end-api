package com.mementee.api.service;

import com.mementee.api.domain.Apply;
import com.mementee.api.domain.Board;
import com.mementee.api.domain.Matching;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.domain.enumtype.ApplyState;
import com.mementee.api.repository.MatchingRepository;
import com.mementee.api.repository.chat.ChatRoomRepository;
import com.mementee.api.validation.MemberValidation;
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
        Apply apply = applyService.findApplyById(applyId);
        Board board = apply.getBoard();

        isCheckCompleteApply(apply);

        LocalDate consultDate = apply.getDate();                        //신청할 때 등록한 날짜
        LocalTime consultTime = apply.getStartTime();                   //신청할 때 등록한 시작 시간

        //신청 받은(게시물 글쓴이) 사람의 Id와 로그인한 사람(나)가 같은지
        MemberValidation.isCheckMe(memberService.findMemberByToken(authorizationHeader), apply.getReceiveMember());

        Member mentor = apply.getReceiveMember();;
        Member mentee = apply.getSendMember();

        Matching matching = new Matching(consultDate, consultTime, board.getConsultTime(),
                board, apply, mentor, mentee);

        matchingRepository.save(matching);

        if(chatRoomRepository.findChatRoomBySenderAndReceiver(mentee, mentor).isEmpty()){
            ChatRoom chatRoom = new ChatRoom(mentor, mentee, matching);
            chatRoomRepository.save(chatRoom);
        }

        board.addUnavailableTimes(consultDate, consultTime);
        apply.updateState();
    }

    //거절-----
    @Transactional
    public void declineMatching(Long applyId, String authorizationHeader){
        Apply apply = applyService.findApplyById(applyId);

        isCheckCompleteApply(apply);

        //신청 받은(게시물 글쓴이) 사람과 로그인한 사람
        MemberValidation.isCheckMe(memberService.findMemberByToken(authorizationHeader), apply.getReceiveMember());
        apply.updateState();
    }

    @Transactional
    public void extendConsultTime(Matching matching){
        matching.extendConsultTime();
    }

    //멘토/멘티 매칭 목록
    public List<Matching> findMatchingsByMember(BoardType boardType, String authorizationHeader) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        if(boardType.equals(BoardType.MENTOR)){
            return matchingRepository.findMatchingsByMentee(member);
        }
        return matchingRepository.findMatchingsByMentor(member);
    }
}
