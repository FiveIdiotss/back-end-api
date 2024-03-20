package com.mementee.api.service;

import com.mementee.api.domain.*;
import com.mementee.api.dto.memberDTO.*;
import com.mementee.api.repository.MemberRepository;
import com.mementee.s3.S3Service;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import com.mementee.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    @Value("${spring.jwt.secret}")      //JWT에 필요한 Key
    private String secretKey;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    private final RefreshTokenService refreshTokenService;
    private final SchoolService schoolService;
    private final MajorService majorService;
    private final BlackListTokenService blackListTokenService;
    private final S3Service s3Service;

    public Member getMemberByToken(String authorizationHeader) {
        String token = authorizationHeader.split(" ")[1];
        String email = JwtUtil.getMemberEmail(token, secretKey);
        return findMemberByEmail(email);
    }

    public Member getMemberById(Long id) {
        try {
            return memberRepository.findOne(id);
        } catch (NoResultException e) {
            throw new NoSuchElementException("특정 멤버가 존재하지 않습니다.");
        }
    }

    //현재 로그인한 유저와 내 정보에 대한 일치 여부
    public Member isCheckMe(String authorizationHeader, Long memberId) {
        Member serverMember = getMemberByToken(authorizationHeader);
        Member member = findOne(memberId);

        if (member != serverMember)
            throw new IllegalArgumentException("권한 없음.");
        return member;
    }

    //비밀번호 맞는지 체크
    public void matchPassWord(String requestPw, String memberPw) {
        if (!passwordEncoder.matches(requestPw, memberPw))
            throw new IllegalArgumentException("비밀번호 불 일치");
    }

    //중복 이메일 검증
    public void emailDuplicateCheck(String email) {
        List<Member> findMembers = memberRepository.emailDuplicateCheck(email);
        if (!findMembers.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    //로그인 시 회원 정보
    public MemberDTO getMemberDTO(Member member) {
        return new MemberDTO(member.getId(), member.getEmail(), member.getName(), member.getYear()
                , member.getGender(), member.getSchool().getName(), member.getMajor().getName(), member.getMemberImage().getPhotoUrl());
    }

    //로그인 시 토큰
    public TokenDTO getTokenDTO(Member member) {
        String newAccessToken = JwtUtil.createAccessToken(member.getEmail(), secretKey);
        String newRefreshToken = JwtUtil.createRefreshToken(secretKey);
        return new TokenDTO(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void join(CreateMemberRequest request) {
        School school = schoolService.findNameOne(request.getSchoolName());
        Major major = majorService.findOne(request.getMajorId());

        emailDuplicateCheck(request.getEmail());

        //회원가입시 기본 이미지로 설정
        String defaultPhotoUrl = s3Service.getImageUrl("defaultImage.jpg");
        MemberImage memberImage = new MemberImage(defaultPhotoUrl);

        String encodePw = passwordEncoder.encode(request.getPassword()); //비밀번호 암호화
        Member member = new Member(request.getEmail(), request.getName(), encodePw, request.getYear(),
                request.getGender(), school, major, memberImage);

        school.getMembers().add(member);
        major.getMembers().add(member);

        memberRepository.save(member);
        memberRepository.saveMemberImage(memberImage);
    }

    //회원 조회
    private Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //로그인 시 이메일로 회원 조회
    public Member findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email);
    }

    @Transactional
    public LoginMemberResponse login(LoginMemberRequest request) {
        Member member = findMemberByEmail(request.getEmail());
        matchPassWord(request.getPassword(), member.getPassword());

        TokenDTO tokenDTO = getTokenDTO(member);

        Optional<RefreshToken> token = refreshTokenService.findRefreshTokenByEmail(member.getEmail());

        if (token.isPresent()) {
            token.get().updateToken(tokenDTO.getRefreshToken());
        } else {
            RefreshToken newToken = new RefreshToken(tokenDTO.getRefreshToken(), member.getEmail());
            refreshTokenService.save(newToken);
        }

        return new LoginMemberResponse(getMemberDTO(member), tokenDTO);
    }

    @Transactional
    public void logout(String authorizationHeader) {
        String accessToken = authorizationHeader.split(" ")[1];

        Member member = getMemberByToken(authorizationHeader);
        Optional<RefreshToken> refreshToken = refreshTokenService.findRefreshTokenByEmail(member.getEmail());

        blackListTokenService.addBlackList(accessToken);
        refreshTokenService.deleteRefreshToken(refreshToken);
    }
}
