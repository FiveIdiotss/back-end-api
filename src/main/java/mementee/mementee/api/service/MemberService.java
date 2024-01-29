package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.memberDTO.*;
import mementee.mementee.api.domain.Major;
import mementee.mementee.api.domain.Member;
import mementee.mementee.api.domain.RefreshToken;
import mementee.mementee.api.domain.School;
import mementee.mementee.api.repository.MemberRepository;
import mementee.mementee.api.repository.RefreshTokenRepository;
import mementee.mementee.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    @Value("${spring.jwt.secret}")      //JWT에 필요한 Key
    private String secretKey;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SchoolService schoolService;
    private final MajorService majorService;

    public Member getMemberByToken(String authorizationHeader){
        String token = authorizationHeader.split(" ")[1];
        String email = JwtUtil.getMemberEmail(token, secretKey);
        return findMemberByEmail(email);
    }


    //비밀번호 맞는지 체크
    public void matchPassWord(String requestPw, String memberPw){
         if(!passwordEncoder.matches(requestPw, memberPw))
             throw new IllegalArgumentException("비밀번호 불 일치");
    }

    //중복 이메일 검증
    public void emailDuplicateCheck(String email){
        List<Member> findMembers = memberRepository.emailDuplicateCheck(email);
        if(!findMembers.isEmpty()){
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    //로그인 시 회원 정보
    public MemberDTO getMemberDTO(Member member){
        return new MemberDTO(member.getId(), member.getEmail(), member.getName(), member.getYear()
                ,member.getGender(), member.getSchool().getName(), member.getMajor().getName());
    }

    //로그인 시 토큰
    public TokenDTO getTokenDTO(Member member){
        String newAccessToken = JwtUtil.createAccessToken(member.getEmail(), secretKey);
        String newRefreshToken = JwtUtil.createRefreshToken(secretKey);
        return new TokenDTO(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void join(CreateMemberRequest request) {
        School school = schoolService.findNameOne(request.getSchoolName());
        Major major = majorService.findOne(request.getMajorId());

        emailDuplicateCheck(request.getEmail());

        String encodePw = passwordEncoder.encode(request.getPassword()); //비밀번호 암호화
        Member member = new Member(request.getEmail(), request.getName(), encodePw, request.getYear(),
                request.getGender(), school, major);

        school.getMembers().add(member);
        major.getMembers().add(member);

        memberRepository.save(member);
    }

    //회원 조회
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //로그인 시 이메일로 회원 조회
    public Member findMemberByEmail(String email){
        return memberRepository.findMemberByEmail(email);
    }

    @Transactional
    public LoginMemberResponse login(LoginMemberRequest request){
        //인증 과정 추가
        Member member = findMemberByEmail(request.getEmail());
        matchPassWord(request.getPassword(), member.getPassword());

        TokenDTO tokenDTO = getTokenDTO(member);

        Optional<RefreshToken> token = refreshTokenRepository.findRefreshTokenByEmail(member.getEmail());

        if(token.isPresent()){
            refreshTokenRepository.save((token.get().updateToken(tokenDTO.getRefreshToken())));
        }else {
            RefreshToken newToken = new RefreshToken(tokenDTO.getRefreshToken(), member.getEmail());
            refreshTokenRepository.save(newToken);
        }

        return new LoginMemberResponse(getMemberDTO(member), tokenDTO, "로그인 성공");
    }
}
