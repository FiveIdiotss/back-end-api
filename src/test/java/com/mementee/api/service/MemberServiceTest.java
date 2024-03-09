//package com.mementee.api.service;
//
//import com.mementee.api.controller.memberDTO.CreateMemberRequest;
//import com.mementee.api.controller.memberDTO.LoginMemberRequest;
//import com.mementee.api.controller.memberDTO.LoginMemberResponse;
//import com.mementee.api.controller.memberDTO.TokenDTO;
//import com.mementee.api.domain.Member;
//import com.mementee.api.domain.RefreshToken;
//import com.mementee.api.repository.MemberRepository;
//import com.mementee.security.JwtUtil;
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.persistence.EntityManager;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.Optional;
//
//import static com.mementee.api.domain.enumtype.Gender.MALE;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@Transactional
//class MemberServiceTest {
//
//    @Autowired MemberService memberService;
//    @Autowired MemberRepository memberRepository;
//
//    @Autowired BoardService boardService;
//    @Autowired BlackListTokenService blackListTokenService;
//    @Autowired RefreshTokenService refreshTokenService;
//
//    @Autowired EntityManager em;
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
//        CreateMemberRequest request2 = new CreateMemberRequest("1234", "1234",   //1234로 회원가입이 됐기 때문에 중복 email 예외발생
//                "1234", 2018, MALE, "가천대학교", 1L);
//
//        assertThrows(IllegalArgumentException.class, () -> memberService.join(request2));
//    }
//
//    @Test
//    void 로그인() throws Exception {
//        //given
//        LoginMemberRequest request = new LoginMemberRequest("1234", "1234");
//
//        //when
//        LoginMemberResponse response = memberService.login(request);
//
//        //then
//        Member member1 = memberService.findMemberByEmail(response.getMemberDTO().getEmail());
//        Member member2 = memberService.findMemberByEmail(request.getEmail());
//        assertEquals(member1, member2);
//    }
//
//    @Test
//    void 로그인_실패() throws Exception {
//        //given
//        LoginMemberRequest request = new LoginMemberRequest("1234", "12345");
//
//        //when&then
//        assertThrows(IllegalArgumentException.class, () -> memberService.login(request));
//    }
//
//    @Test
//    void 로그아웃() throws Exception {
//        //given
//        LoginMemberRequest request = new LoginMemberRequest("1234", "1234");
//
//        //when
//        memberService.login(request);
//        Member member = memberService.findMemberByEmail(request.getEmail());
//
//        TokenDTO tokenDTO = memberService.getTokenDTO(member);
//        String authorizationHeader = "Bearer " + tokenDTO.getAccessToken();
//        memberService.logout(authorizationHeader);
//
//        //then
//        assertEquals(member, memberRepository.findOne(1L));
//
//        // 로그아웃시 refresh토큰이 잘 지워졌는지
//        Optional<RefreshToken> refreshToken = refreshTokenService.findRefreshTokenByEmail(member.getEmail());
//        assertFalse(refreshToken.isPresent());
//
//        // 로그아웃시 BlackList에 잘 들어 갔는지 확인
//        assertTrue(blackListTokenService.isCheckBlackList(tokenDTO.getAccessToken()));
//    }
//
//}