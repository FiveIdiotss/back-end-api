//package com.mementee.api.service;
//
//import com.team.enumtype.domain.api.mementee.BoardCategory;
//import com.team.subdomain.domain.api.mementee.ScheduleTime;
//import com.team.applyDTO.dto.api.mementee.ApplyRequest;
//import com.team.boardDTO.dto.api.mementee.WriteBoardRequest;
//import com.team.memberDTO.dto.api.mementee.CreateMemberRequest;
//import com.team.memberDTO.dto.api.mementee.LoginMemberRequest;
//import com.team.memberDTO.dto.api.mementee.LoginMemberResponse;
//import com.team.repository.api.mementee.ApplyRepository;
//import com.team.board.repository.api.mementee.BoardRepository;
//import com.team.member.repository.api.mementee.MemberRepository;
//import com.team.conflict.exception.mementee.ApplyConflictException;
//import com.team.conflict.exception.mementee.MyApplyConflictException;
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
//import static com.team.enumtype.domain.api.mementee.Gender.MALE;
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