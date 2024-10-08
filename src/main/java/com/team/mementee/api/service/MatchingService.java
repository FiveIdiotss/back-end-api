package com.team.mementee.api.service;

import com.team.mementee.api.domain.Apply;
import com.team.mementee.api.domain.Board;
import com.team.mementee.api.domain.Matching;
import com.team.mementee.api.domain.chat.ChatRoom;
import com.team.mementee.api.dto.applyDTO.ReasonOfRejectRequest;
import com.team.mementee.api.repository.MatchingRepository;
import com.team.mementee.api.repository.chat.ChatRoomRepository;
import com.team.mementee.api.validation.MatchingValidation;
import com.team.mementee.api.validation.MemberValidation;
import lombok.RequiredArgsConstructor;
import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.enumtype.BoardType;
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

    //수락 기능----------
    @Transactional
    public void saveMatching(Long applyId, String authorizationHeader) {
        Apply apply = applyService.findApplyById(applyId);
        Board board = apply.getBoard();

        MatchingValidation.isCheckCompleteApply(apply);

        LocalDate consultDate = apply.getDate();                        //신청할 때 등록한 날짜
        LocalTime consultTime = apply.getStartTime();                   //신청할 때 등록한 시작 시간

        //신청 받은(게시물 글쓴이) 사람의 Id와 로그인한 사람(나)가 같은지
        MemberValidation.isCheckMe(memberService.findMemberByToken(authorizationHeader), apply.getReceiveMember());

        Member mentor = apply.getReceiveMember();
        Member mentee = apply.getSendMember();

        Matching matching = new Matching(consultDate, consultTime, board.getConsultTime(),
                board, apply, mentor, mentee);

        matchingRepository.save(matching);

        if (chatRoomRepository.findChatRoomBySenderAndReceiver(mentee, mentor).isEmpty()) {
            ChatRoom chatRoom = new ChatRoom(mentor, mentee, matching);
            chatRoomRepository.save(chatRoom);
        }

        mentor.addConsultCount();
        board.addUnavailableTimes(consultDate, consultTime);
        apply.updateCompleteState();
    }

    //거절-----
    @Transactional
    public void declineMatching(Long applyId, ReasonOfRejectRequest request, String authorizationHeader) {
        Apply apply = applyService.findApplyById(applyId);
        MatchingValidation.isCheckCompleteApply(apply);
        MemberValidation.isCheckMe(memberService.findMemberByToken(authorizationHeader), apply.getReceiveMember());
        apply.updateRejectState();
        apply.getApplyState().reasonOfReject(request.getContent());
    }

    @Transactional
    public void extendConsultTime(Matching matching) {
        matching.extendConsultTime();
    }

    //멘토/멘티 매칭 목록
    public List<Matching> findMatchingsByMember(BoardType boardType, String authorizationHeader) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        if (boardType.equals(BoardType.MENTOR)) {
            return matchingRepository.findMatchingsByMentee(member);
        }
        return matchingRepository.findMatchingsByMentor(member);
    }
}
