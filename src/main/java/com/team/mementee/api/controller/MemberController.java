package com.team.mementee.api.controller;

import com.team.mementee.api.dto.CommonApiResponse;
import com.team.mementee.api.dto.memberDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.team.mementee.api.domain.Member;
import com.team.mementee.api.service.MajorService;
import com.team.mementee.api.service.MemberService;
import com.team.mementee.api.service.SchoolService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "회원 관련 -(회원 가입, 회원 조회, 로그인, 학교 조회, 전공 조회)")
public class MemberController {

    private final MemberService memberService;
    private final SchoolService schoolService;
    private final MajorService majorService;


    //회원가입--------------------------------------
    @Operation(summary = "회원 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping("/api/member/signUp")
    public CommonApiResponse<?> joinMember(@RequestBody @Valid CreateMemberRequest request) {
        memberService.join(request);
        return CommonApiResponse.createSuccess();
    }


    //목록 조회--------------------------------------
    @Operation(summary = "학교 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/schools")
    public CommonApiResponse<List<SchoolDTO>> schoolList() {
        List<SchoolDTO> collect = SchoolDTO.createSchoolDTOs(schoolService.findAll());
        return CommonApiResponse.createSuccess(collect);
    }

    //해당 학교에 속하는 과 --------------------------------------
    @Operation(summary = "학교에 속한 전공 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/school/{schoolName}")
    public CommonApiResponse<List<MajorDTO>> majorList(@PathVariable String schoolName) {
        List<MajorDTO> collect = MajorDTO.createMajorDTOs(majorService.findAllBySchoolName(schoolName));
        return CommonApiResponse.createSuccess(collect);
    }


    //로그인--------------------------------------
    @Operation(summary = "로그인 - (access token 기간 1시간)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "로그인 성공"),
            @ApiResponse(responseCode = "fail", description = "로그인 실패")})
    @PostMapping("/api/member/signIn")
    public CommonApiResponse<LoginMemberResponse> signIn(@Valid @RequestBody LoginMemberRequest request) {
        return CommonApiResponse.createSuccess(memberService.login(request));
    }

    //로그아웃-----------------
    @Operation(summary = "로그아웃")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "fail", description = "로그아웃 실패")})
    @PostMapping("/api/member/signOut")
    public CommonApiResponse<?> signOut(@RequestHeader("Authorization") String authorizationHeader) {
        memberService.logout(authorizationHeader);
        return CommonApiResponse.createSuccess();
    }

    //회원 정보-----------------------------------
    @Operation(summary = "특정 회원 정보")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "회원 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "회원 조회 실패")})
    @GetMapping("/api/member/{memberId}")
    public CommonApiResponse<MemberInfoResponse> memberInfo(@PathVariable Long memberId) {
        Member member = memberService.findMemberById(memberId);
        return CommonApiResponse.createSuccess(MemberInfoResponse.createMemberInfoResponse(member));
    }

    //프로필 변경
    @Operation(summary = "프로필 사진 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "프로필 변경 성공"),
            @ApiResponse(responseCode = "fail", description = "프로필 변경 실패")})
    @PostMapping(value = "/api/member/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonApiResponse<String> updatedMemberImage(@RequestHeader("Authorization") String authorizationHeader,
                                                        @RequestPart("imageFile") MultipartFile multipartFile) {
        String imageUrl = memberService.updatedMemberImage(authorizationHeader, multipartFile);
        return CommonApiResponse.createSuccess(imageUrl);
    }

    @Operation(summary = "프로필 기본 사진으로 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "프로필 변경 성공"),
            @ApiResponse(responseCode = "fail", description = "프로필 변경 실패")})
    @PostMapping("/api/member/defaultImage")
    public CommonApiResponse<String> updatedDefaultMemberImage(@RequestHeader("Authorization") String authorizationHeader) {
        String imageUrl = memberService.updatedDefaultMemberImage(authorizationHeader);
        return CommonApiResponse.createSuccess(imageUrl);
    }

    //비밀번호 변경 전 검증
    @Operation(summary = "비밀번호 변경 시 / 현재 비밀번호가 로그인한 비밀번호와 맞는지")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "프로필 변경 성공"),
            @ApiResponse(responseCode = "fail", description = "프로필 변경 실패")})
    @PostMapping("/api/member/pw")
    public CommonApiResponse<?> isPassword(@RequestHeader("Authorization") String authorizationHeader,
                                           @RequestBody PasswordRequest request) {
        memberService.isMatchPassWord(authorizationHeader, request);
        return CommonApiResponse.createSuccess();
    }

    //비밀번호 변경
    @Operation(summary = "비밀번호 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "프로필 변경 성공"),
            @ApiResponse(responseCode = "fail", description = "프로필 변경 실패")})
    @PutMapping("/api/member/pw")
    public CommonApiResponse<?> changePassword(@RequestHeader("Authorization") String authorizationHeader,
                                               @RequestBody PasswordRequest request) {
        memberService.changePassWord(authorizationHeader, request);
        return CommonApiResponse.createSuccess();
    }
}
