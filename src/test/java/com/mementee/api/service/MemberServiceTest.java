//package com.mementee.api.service;
//
//import com.team.domain.api.mementee.Member;
//import com.team.domain.api.mementee.RefreshToken;
//import com.mementee.api.dto.memberDTO.*;
//import com.team.member.repository.api.mementee.MemberRepository;
//import com.team.member.repository.api.mementee.SchoolRepository;
//import com.team.conflict.exception.mementee.EmailConflictException;
//import com.team.unauthorized.exception.mementee.LoginFailedException;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.Optional;
//
//import static com.team.enumtype.domain.api.mementee.Gender.MALE;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = MemberServiceTest.class)
//@Transactional
//class MemberServiceTest {
//
//    @Autowired MemberService memberService;
//    @Autowired SchoolRepository schoolRepository;
//
//    @Autowired MemberRepository memberRepository;
//
//    @Autowired BoardService boardService;
//    @Autowired BlackListTokenService blackListTokenService;
//    @Autowired RefreshTokenService refreshTokenService;
//
//    @Test
//    void 회원가입() throws Exception{    //회원가입 후 같은 email로 회원 가입시 실패
//        //given
//        CreateMemberRequest request1 = new CreateMemberRequest("test", "test",
//                "test", 2018, MALE, "가천대학교", 1L);
//
//        //when
//        memberService.join(request1);
//
//        //then
//        CreateMemberRequest request2 = new CreateMemberRequest("test", "test",
//                "1234", 2018, MALE, "가천대학교", 1L);
//        assertThrows(EmailConflictException.class, () -> memberService.join(request2));
//    }
//
//    @Test
//    void 로그인() throws Exception {
//        //given
//        CreateMemberRequest request1 = new CreateMemberRequest("test", "test",
//                "test", 2018, MALE, "가천대학교", 1L);
//
//        //when
//        memberService.join(request1);
//        LoginMemberRequest request = new LoginMemberRequest("test", "test");
//        LoginMemberResponse response = memberService.login(request);
//
//        //then
//        Member member1 = memberService.findMemberByEmail(response.getMemberDTO().getEmail());   //로그인 반환 값
//        Member member2 = memberService.findMemberByEmail(request.getEmail());                   //내가 입력한 email로 로그인
//        assertEquals(member1, member2);
//    }
//
//    @Test
//    void 로그인_실패() throws Exception {
//        //given
//        CreateMemberRequest createRequest = new CreateMemberRequest("test", "test",
//                "test", 2018, MALE, "가천대학교", 1L);
//
//        //when
//        memberService.join(createRequest);
//
//        //given
//        LoginMemberRequest request = new LoginMemberRequest("test", "test5");
//        assertThrows(LoginFailedException.class, () -> memberService.login(request));
//    }
//
//    @Test
//    void 로그아웃() throws Exception {
//        //given
//        CreateMemberRequest createRequest = new CreateMemberRequest("test", "test",
//                "test", 2018, MALE, "가천대학교", 1L);
//        memberService.join(createRequest);
//        LoginMemberRequest request = new LoginMemberRequest("test", "test");
//        LoginMemberResponse response = memberService.login(request);
//        TokenDTO tokenDTO = response.getTokenDTO();
//        MemberDTO memberDTO = response.getMemberDTO();
//
//        //when
//        Member loginMember = memberService.findMemberById(memberDTO.getId());
//        String authorizationHeader = "Bearer " + tokenDTO.getAccessToken();
//        assertEquals(loginMember, memberService.findMemberByToken(authorizationHeader));
//        memberService.logout(authorizationHeader);
//
//        //then
//        // 로그아웃시 refresh토큰이 잘 지워졌는지
//        Optional<RefreshToken> refreshToken = refreshTokenService.findRefreshTokenByMember(loginMember);
//        assertFalse(refreshToken.isPresent());
//
//        // 로그아웃시 BlackList에 잘 들어 갔는지 확인
//        assertTrue(blackListTokenService.isCheckBlackList(tokenDTO.getAccessToken()));
//    }
//
//    @Test
//    void 비밀번호_변경() throws Exception {
//        //given
//        CreateMemberRequest createRequest = new CreateMemberRequest("test", "test",
//                "test", 2018, MALE, "가천대학교", 1L);
//        memberService.join(createRequest);
//        LoginMemberRequest loginRequest = new LoginMemberRequest("test", "test");
//        LoginMemberResponse loginResponse = memberService.login(loginRequest);
//        TokenDTO tokenDTO = loginResponse.getTokenDTO();
//        String authorizationHeader = "Bearer " + tokenDTO.getAccessToken();
//
//        //when
//        PasswordRequest passwordRequest = new PasswordRequest("test3");
//
//        //then
//        memberService.changePassWord(authorizationHeader, passwordRequest);
//        assertThrows(LoginFailedException.class, () -> memberService.login(loginRequest));
//    }
//
//}