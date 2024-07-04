package com.team.mementee.api.controller;

import com.team.mementee.api.dto.CommonApiResponse;
import com.team.mementee.api.dto.memberDTO.CreateMemberRequest;
import com.team.mementee.api.dto.memberDTO.MemberDTO;
import com.team.mementee.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "관리자용 api")
@Slf4j
public class AdminController {

    private final MemberService memberService;

    @Operation(summary = "관리자 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "등록 성공"),
            @ApiResponse(responseCode = "fail", description = "등록 실패")})
    @PostMapping("/api/admin/signUp")
    public CommonApiResponse<?> adminJoinMember(@RequestBody @Valid CreateMemberRequest request) {
        memberService.adminJoin(request);
        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "모든 회원 조회 / admin 용")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/admin/members")
    public CommonApiResponse<List<MemberDTO>> memberList() {
        List<MemberDTO> collect = MemberDTO.createMemberDTOs(memberService.findAll());
        return CommonApiResponse.createSuccess(collect);
    }
}
