package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.memberDTO.TokenDTO;
import mementee.mementee.api.domain.Major;
import mementee.mementee.api.domain.Member;
import mementee.mementee.api.domain.RefreshToken;
import mementee.mementee.api.domain.School;
import mementee.mementee.api.repository.MemberRepository;
import mementee.mementee.api.repository.RefreshTokenRepository;
import mementee.mementee.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
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
    //private Long expiredMs = 1000 * 60 * 60l; // access 토큰 1시간 만료 시간
    //private Long expiredMs = 1000 * 60l; //1시간

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void join(Member member, School school, Major major) {
        school.getMembers().add(member);
        major.getMembers().add(member);

        memberRepository.save(member);
    }

    //중복 이메일 검증
    public void emailDuplicateCheck(String email){
        List<Member> findMembers = memberRepository.emailDuplicateCheck(email);
        if(!findMembers.isEmpty()){
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    //회원 하나 조회
    public Member findOne(Long memberId) {
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
    public TokenDTO login(Member member){
        //인증 과정 추가
        Optional<RefreshToken> token = refreshTokenRepository.findRefreshTokenByEmail(member.getEmail());

        String newAccessToken = JwtUtil.createAccessToken(member.getEmail(), secretKey);
        String newRefreshToken = JwtUtil.createRefreshToken(secretKey);

        if(token.isPresent()){
            refreshTokenRepository.save((token.get().updateToken(newRefreshToken)));
        }else {
            RefreshToken newToken = new RefreshToken(newRefreshToken, member.getEmail());
            refreshTokenRepository.save(newToken);
        }

        return new TokenDTO(newAccessToken, newRefreshToken);
    }
}
