//package com.mementee.api.service;
//
//import com.mementee.api.controller.applyDTO.ApplyRequest;
//import com.mementee.api.controller.boardDTO.WriteBoardRequest;
//import com.mementee.api.controller.memberDTO.CreateMemberRequest;
//import com.mementee.api.controller.memberDTO.LoginMemberRequest;
//import com.mementee.api.controller.memberDTO.LoginMemberResponse;
//import com.mementee.api.domain.enumtype.BoardType;
//import com.mementee.api.domain.subdomain.ScheduleTime;
//import com.mementee.api.repository.ApplyRepository;
//import com.mementee.api.repository.board.BoardRepository;
//import com.mementee.api.repository.MemberRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//
//import static com.mementee.api.domain.enumtype.Gender.MALE;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@Transactional
//class ApplyServiceTest {
//
//    @Autowired MemberService memberService;
//    @Autowired MemberRepository memberRepository;
//
//    @Autowired BoardService boardService;
//    @Autowired BoardRepository boardRepository;
//
//    @Autowired ApplyRepository applyRepository;
//    @Autowired ApplyService applyService;
//
//    @Test
//    void 멘토_멘티_신청_예외() throws Exception {
//        //given
//        LoginMemberRequest loginRequest1 = new LoginMemberRequest("1234", "1234");
//        LoginMemberResponse response1 = memberService.login(loginRequest1);
//
//        WriteBoardRequest writeRequest = new WriteBoardRequest("test","test", 30, BoardType.MENTOR,
//                List.of(new ScheduleTime(LocalTime.of(3,3,3,3), LocalTime.of(6,3,3,3))),
//                List.of(DayOfWeek.MONDAY));
//
//        String authorizationHeader = "Bearer " + response1.getTokenDTO().getAccessToken();
//        Long boardId = boardService.saveBoard(writeRequest, authorizationHeader);
//
//        //새로 회원가입 후 로그인
//        CreateMemberRequest createRequest = new CreateMemberRequest("test", "test",
//                "test", 2018, MALE, "가천대학교", 1L);
//        memberService.join(createRequest);
//
//        //when
//        LoginMemberRequest loginRequest2 = new LoginMemberRequest("test", "test");
//        LoginMemberResponse response2 = memberService.login(loginRequest2);
//
//        String authorizationHeader2 = "Bearer " + response2.getTokenDTO().getAccessToken();
//
//        //then
//        applyService.sendApply(authorizationHeader2, boardId, new ApplyRequest("testApply", LocalDate.now(), LocalTime.now()));
//
//        //중복신청시 예외
//        assertTrue(assertThrows(IllegalArgumentException.class, () -> applyService.sendApply(authorizationHeader2, boardId,
//                new ApplyRequest("testApply", LocalDate.now(), LocalTime.now())))
//                .getMessage().contains("이미 신청한 글 입니다."));
//
//        //자신의 글에 신청시 예외
//        assertTrue(assertThrows(IllegalArgumentException.class, () -> applyService.sendApply(authorizationHeader, boardId,
//                new ApplyRequest("testApply", LocalDate.now(), LocalTime.now())))
//                .getMessage().contains("자신의 글에는 신청할 수 없습니다."));
//    }
//
//
//
//}