//package com.mementee.api.service;
//
//import com.mementee.api.controller.boardDTO.WriteBoardRequest;
//import com.mementee.api.controller.memberDTO.CreateMemberRequest;
//import com.mementee.api.controller.memberDTO.LoginMemberRequest;
//import com.mementee.api.controller.memberDTO.LoginMemberResponse;
//import com.mementee.api.controller.memberDTO.TokenDTO;
//import com.mementee.api.domain.Board;
//import com.mementee.api.domain.Member;
//import com.mementee.api.domain.enumtype.BoardType;
//import com.mementee.api.domain.subdomain.ScheduleTime;
//import com.mementee.api.repository.board.BoardRepository;
//import com.mementee.api.repository.member.MemberRepository;
//import jakarta.persistence.*;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.DayOfWeek;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static com.mementee.api.domain.enumtype.Gender.MALE;
//import static org.junit.jupiter.api.Assertions.*;
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@Transactional
//class BoardServiceTest {
//
//    @Autowired MemberService memberService;
//    @Autowired MemberRepository memberRepository;
//
//    @Autowired BoardService boardService;
//    @Autowired BoardRepository boardRepository;
//
//    @Test
//    void 글쓰기() throws Exception {
//        //given
//        LoginMemberRequest request = new LoginMemberRequest("1234", "1234");
//        LoginMemberResponse response = memberService.login(request);
//
//        WriteBoardRequest request1 = new WriteBoardRequest("test","test", 30, BoardType.MENTOR,
//                List.of(new ScheduleTime(LocalTime.of(3,3,3,3), LocalTime.of(6,3,3,3))),
//                        List.of(DayOfWeek.MONDAY));
//
//        String authorizationHeader = "Bearer " + response.getTokenDTO().getAccessToken();
//        Member member = memberService.getMemberByToken(authorizationHeader);
//
//        //when
//        Long boardId = boardService.saveBoard(request1, authorizationHeader);
//        Board board = boardService.findBoard(boardId);
//
//        //then
//        assertEquals(member, board.getMember());
//    }
//
//    @Test
//    void 자신이_쓴_글이_아닐경우_수정_불가() throws Exception {
//
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
//        LoginMemberRequest loginRequest2 = new LoginMemberRequest("test", "test");
//        LoginMemberResponse response2 = memberService.login(loginRequest2);
//
//        String authorizationHeader2 = "Bearer " + response2.getTokenDTO().getAccessToken();
//
//        //when & then
//        assertThrows(IllegalArgumentException.class, () -> boardService.modifyBoard(writeRequest, authorizationHeader2, boardId));
//
////        assertTrue(assertThrows(IllegalArgumentException.class, () -> assertThrows(IllegalArgumentException.class, () ->
////                boardService.modifyBoard(writeRequest, authorizationHeader2, boardId)))
////                .getMessage().contains("권한이 없습니다."));
//    }
//}