//package com.mementee.api.service;
//
//import com.mementee.api.domain.enumtype.BoardCategory;
//import com.mementee.api.domain.subdomain.ScheduleTime;
//import com.mementee.api.dto.applyDTO.ApplyRequest;
//import com.mementee.api.dto.boardDTO.WriteBoardRequest;
//import com.mementee.api.dto.memberDTO.CreateMemberRequest;
//import com.mementee.api.dto.memberDTO.LoginMemberRequest;
//import com.mementee.api.dto.memberDTO.LoginMemberResponse;
//import com.mementee.api.repository.ApplyRepository;
//import com.mementee.api.repository.board.BoardRepository;
//import com.mementee.api.repository.member.MemberRepository;
//import com.mementee.exception.conflict.ApplyConflictException;
//import com.mementee.exception.conflict.MyApplyConflictException;
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
//    LoginMemberResponse 회원가입_로그인_글쓰기(){
//        CreateMemberRequest request1 = new CreateMemberRequest("test", "test",
//                "test", 2018, MALE, "가천대학교", 1L);
//        memberService.join(request1);
//
//        LoginMemberRequest request = new LoginMemberRequest("test", "test");
//        LoginMemberResponse response = memberService.login(request);
//        return response;
//    }
//
//    Long 글쓰기_편의메소드(String header){
//        WriteBoardRequest writeRequest = new WriteBoardRequest("test","test", "test", "test",30, BoardCategory.인문,
//                List.of(new ScheduleTime(LocalTime.of(3,3,3,3), LocalTime.of(6,3,3,3))), List.of(DayOfWeek.MONDAY));
//        return boardService.saveBoard(writeRequest, header);
//    }
//
//    @Test
//    void 멘토_멘티_신청_예외() throws Exception {
//        //given
//        LoginMemberResponse writeMember = 회원가입_로그인_글쓰기();
//        String firstHeader = "Bearer " + writeMember.getTokenDTO().getAccessToken();
//        Long boardId = 글쓰기_편의메소드(firstHeader);
//
//        //새로 회원가입 후 로그인
//        CreateMemberRequest createRequest = new CreateMemberRequest("test2", "test2",
//                "test2", 2018, MALE, "가천대학교", 1L);
//        memberService.join(createRequest);
//        LoginMemberRequest secondRequest = new LoginMemberRequest("test2", "test2");
//        LoginMemberResponse secondResponse = memberService.login(secondRequest);
//        String secondHeader = "Bearer " + secondResponse.getTokenDTO().getAccessToken();
//
//        //when&then
//        ApplyRequest request = new ApplyRequest("testApply", LocalDate.now(), LocalTime.now());
//
//        //글쓴이와 신청자가 동일할 때
//        MyApplyConflictException myApplyConflictException = assertThrows(MyApplyConflictException.class, () -> applyService.sendApply(firstHeader, boardId, request));
//        assertTrue(myApplyConflictException.getMessage().contains("자신의 글에는 신청할 수 없습니다."));
//
//        //중복 신청 예외
//        applyService.sendApply(secondHeader, boardId, request);
//        ApplyConflictException conflictException = assertThrows(ApplyConflictException.class, () -> applyService.sendApply(secondHeader, boardId, request));
//        assertTrue(conflictException.getMessage().contains("이미 신청한 게시판 입니다."));
//    }
//
//
//
//}