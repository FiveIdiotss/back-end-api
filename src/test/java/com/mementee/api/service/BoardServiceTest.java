//package com.mementee.api.service;
//
//import com.team.domain.api.mementee.Board;
//import com.team.domain.api.mementee.Member;
//import com.team.enumtype.domain.api.mementee.BoardCategory;
//import com.team.subdomain.domain.api.mementee.ScheduleTime;
//import com.team.boardDTO.dto.api.mementee.WriteBoardRequest;
//import com.team.memberDTO.dto.api.mementee.CreateMemberRequest;
//import com.team.memberDTO.dto.api.mementee.LoginMemberRequest;
//import com.team.memberDTO.dto.api.mementee.LoginMemberResponse;
//import com.team.board.repository.api.mementee.BoardRepository;
//import com.team.member.repository.api.mementee.MemberRepository;
//import com.team.exception.mementee.ForbiddenException;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.DayOfWeek;
//import java.time.LocalTime;
//import java.util.List;
//
//import static com.team.enumtype.domain.api.mementee.Gender.MALE;
//import static org.junit.jupiter.api.Assertions.*;
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@Transactional
//class BoardServiceTest {
//
//    @Autowired MemberService memberService;
//    @Autowired BoardService boardService;
//
//    LoginMemberResponse 회원가입_로그인_편의메소드(){
//        CreateMemberRequest request1 = new CreateMemberRequest("test", "test",
//                "test", 2018, MALE, "가천대학교", 1L);
//        memberService.join(request1);
//
//        LoginMemberRequest request = new LoginMemberRequest("test", "test");
//        LoginMemberResponse response = memberService.login(request);
//        return response;
//    }
//
//    @Test
//    void 글쓰기() throws Exception {
//        //given
//        LoginMemberResponse response = 회원가입_로그인_편의메소드();
//
//        WriteBoardRequest writeRequest = new WriteBoardRequest("test","test", "test", "test",30, BoardCategory.인문,
//                List.of(new ScheduleTime(LocalTime.of(3,3,3,3), LocalTime.of(6,3,3,3))), List.of(DayOfWeek.MONDAY));
//        String authorizationHeader = "Bearer " + response.getTokenDTO().getAccessToken();
//        Member loginMember = memberService.findMemberByToken(authorizationHeader);
//
//        //when
//        Long boardId = boardService.saveBoard(writeRequest, authorizationHeader);
//
//        //then
//        Board board = boardService.findBoardById(boardId);
//        assertEquals(board.getMember(), loginMember);
//    }
//
//    @Test
//    void 자신이_쓴_글이_아닐경우_수정_불가() throws Exception {
//        //given
//        //회원 가입 후 로그인
//        LoginMemberResponse response = 회원가입_로그인_편의메소드();
//        WriteBoardRequest request = new WriteBoardRequest("test","test", "test", "test",30, BoardCategory.인문,
//                List.of(new ScheduleTime(LocalTime.of(3,3,3,3), LocalTime.of(6,3,3,3))), List.of(DayOfWeek.MONDAY));
//        String firstToken = "Bearer " + response.getTokenDTO().getAccessToken();
//        Long boardId = boardService.saveBoard(request, firstToken);
//
//        //새로 회원가입 후 로그인
//        CreateMemberRequest createRequest = new CreateMemberRequest("test2", "test2",
//                "test2", 2018, MALE, "가천대학교", 1L);
//        memberService.join(createRequest);
//        LoginMemberRequest secondLoginRequest = new LoginMemberRequest("test2", "test2");
//        LoginMemberResponse secondResponse = memberService.login(secondLoginRequest);
//        String secondToken = "Bearer " + secondResponse.getTokenDTO().getAccessToken();
//        WriteBoardRequest modifyRequest = new WriteBoardRequest("test2","test2", "test2", "test2",30, BoardCategory.인문,
//                List.of(new ScheduleTime(LocalTime.of(3,3,3,3), LocalTime.of(6,3,3,3))), List.of(DayOfWeek.MONDAY));
//
//        //when&then
//        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> boardService.modifyBoard(modifyRequest, secondToken, boardId));
//        assertTrue(exception.getMessage().contains("권한 없음"));
//    }
//}