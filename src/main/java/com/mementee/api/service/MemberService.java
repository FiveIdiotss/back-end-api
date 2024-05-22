package com.mementee.api.service;

import com.mementee.api.domain.*;
import com.mementee.api.dto.memberDTO.*;
import com.mementee.api.repository.member.MemberRepository;
import com.mementee.api.validation.MemberValidation;
import com.mementee.exception.ForbiddenException;
import com.mementee.exception.conflict.EmailConflictException;
import com.mementee.exception.unauthorized.LoginFailedException;
import com.mementee.exception.conflict.ProfileConflictException;
import com.mementee.exception.notFound.MemberNotFound;
import com.mementee.s3.S3Service;
import lombok.RequiredArgsConstructor;
import com.mementee.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    private final RefreshTokenService refreshTokenService;
    private final SchoolService schoolService;
    private final MajorService majorService;
    private final BlackListTokenService blackListTokenService;
    private final S3Service s3Service;

    //로그인 시 토큰 TokenDTO 발급
    public TokenDTO createTokenDTO(Member member) {
        String newAccessToken = JwtUtil.createAccessToken(member.getEmail(), secretKey);
        String newRefreshToken = JwtUtil.createRefreshToken(secretKey);
        return new TokenDTO(newAccessToken, newRefreshToken);
    }

    //토큰으로 회원 찾기
    public Member findMemberByToken(String authorizationHeader) {
        String token = authorizationHeader.split(" ")[1];
        String email = JwtUtil.getMemberEmail(token, secretKey);
        return findMemberByEmail(email);
    }

    //회원 id 값으로 조회
    public Member findMemberById(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if(member.isEmpty())
            throw new MemberNotFound();
        return member.get();
    }


    //로그인 시 이메일로 회원 조회
    public Member findMemberByEmail(String email) {
        Optional<Member> member = memberRepository.findMemberByEmail(email);
        if(member.isEmpty())
            throw new MemberNotFound();
        return member.get();
    }

    //회원 전체 조회
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    //회원 가입, 로그인, 로그아웃
    @Transactional
    public void join(CreateMemberRequest request) {
        School school = schoolService.findSchoolByName(request.getSchoolName());
        Major major = majorService.findMajorById(request.getMajorId());

        //이미 있는 Email 있는지
        MemberValidation.isDuplicateCheck(memberRepository.findMemberByEmail(request.getEmail()));

        //회원가입시 기본 이미지로 설정
        String defaultPhotoUrl = s3Service.getImageUrl("defaultImage.jpg");

        String encodePw = passwordEncoder.encode(request.getPassword()); //비밀번호 암호화
        Member member = new Member(request.getEmail(), request.getName(), encodePw, request.getYear(),
                defaultPhotoUrl, request.getGender(), school, major);

        school.getMembers().add(member);
        major.getMembers().add(member);

        memberRepository.save(member);
    }

    @Transactional
    public LoginMemberResponse login(LoginMemberRequest request) {
        Member member = findMemberByEmail(request.getEmail());

        MemberValidation.isMatchPassWord(passwordEncoder.matches(request.getPassword(), member.getPassword()));

        TokenDTO tokenDTO = createTokenDTO(member);
        Optional<RefreshToken> token = refreshTokenService.findRefreshTokenByEmail(member.getEmail());

        if (token.isPresent()) {
            token.get().updateToken(tokenDTO.getRefreshToken());
        } else {
            RefreshToken newToken = new RefreshToken(tokenDTO.getRefreshToken(), member.getEmail());
            refreshTokenService.save(newToken);
        }
        return LoginMemberResponse.createLoginMemberResponse(MemberDTO.createMemberDTO(member), tokenDTO);
    }

    @Transactional
    public void logout(String authorizationHeader) {
        String accessToken = authorizationHeader.split(" ")[1];

        Member member = findMemberByToken(authorizationHeader);
        Optional<RefreshToken> refreshToken = refreshTokenService.findRefreshTokenByEmail(member.getEmail());

        blackListTokenService.addBlackList(accessToken);
        refreshTokenService.deleteRefreshToken(refreshToken);
    }

    //프로필 사진 변경
    @Transactional
    public String updatedMemberImage(String authorizationHeader, MultipartFile image){
        Member member = findMemberByToken(authorizationHeader);

        String imageUrl = s3Service.saveFile(image);
        member.updateMemberImage(imageUrl);
        return imageUrl;
    }

    //프로필 기본 이미지로 변경
    @Transactional
    public String updatedDefaultMemberImage(String authorizationHeader) {
        Member member = findMemberByToken(authorizationHeader);
        String imageUrl = s3Service.getImageUrl("defaultImage.jpg");

        MemberValidation.isCheckDefaultImage(member, imageUrl);
        member.updateMemberImage(imageUrl);
        return imageUrl;
    }
}
