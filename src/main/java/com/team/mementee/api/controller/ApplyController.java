package com.team.mementee.api.controller;

import com.team.mementee.api.domain.Apply;
import com.team.mementee.api.dto.CommonApiResponse;
import com.mementee.api.dto.applyDTO.*;
import com.team.mementee.api.dto.PageInfo;
import com.team.mementee.api.dto.applyDTO.ApplyDTO;
import com.team.mementee.api.dto.applyDTO.ApplyInfoResponse;
import com.team.mementee.api.dto.applyDTO.PaginationApplyResponse;
import com.team.mementee.api.service.ApplyService;
import com.team.mementee.api.service.MatchingService;
import com.team.mementee.api.validation.ApplyValidation;
import com.team.mementee.api.domain.enumtype.SendReceive;
import com.team.mementee.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "멘토/멘티  신청 리스트 조회, 수락/거절 기능")
@Slf4j
public class ApplyController {

    private final ApplyService applyService;
    private final MemberService memberService;
    private final MatchingService matchingService;

    @Operation(summary = "내가 신청 한/받은 글 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/myApply")
    public CommonApiResponse<List<ApplyDTO>> applyList(@RequestHeader("Authorization") String authorizationHeader,
                                                       @RequestParam SendReceive sendReceive){
        List<Apply> list = applyService.findMyApply(memberService.findMemberByToken(authorizationHeader), sendReceive);
        if(sendReceive == SendReceive.RECEIVE) {
            return CommonApiResponse.createSuccess(ApplyDTO.createReceiveApplyDTOs(list));
        }
        return CommonApiResponse.createSuccess(ApplyDTO.createSendApplyDTOs(list));
    }

    @Operation(summary=  "page 를 통한 내가 신청 한/받은 글 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/pageMyApply")
    public CommonApiResponse<?> pageApplyList(@RequestHeader("Authorization") String authorizationHeader,
                                              @RequestParam SendReceive sendReceive,
                                              @RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Page<Apply> findApplies = applyService.findMyApplyByPage(memberService.findMemberByToken(authorizationHeader), sendReceive, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)findApplies.getTotalElements(), findApplies.getTotalPages());
        List<Apply> response = findApplies.getContent();

        if(sendReceive == SendReceive.RECEIVE) {
            List<ApplyDTO> list = ApplyDTO.createReceiveApplyDTOs(response);
            return CommonApiResponse.createSuccess(new PaginationApplyResponse(list, pageInfo));
        }
        List<ApplyDTO> list = ApplyDTO.createSendApplyDTOs(response);
        return CommonApiResponse.createSuccess((new PaginationApplyResponse(list, pageInfo)));
    }

    //신청 받기
    @Operation(summary = "멘티 신청 수락, 거절과 통합 예정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "신청 성공"),
            @ApiResponse(responseCode = "fail", description = "신청 실패")})
    @PostMapping("/api/apply/{applyId}")
    public CommonApiResponse<?> boardApply(@RequestHeader("Authorization") String authorizationHeader,
                                           @PathVariable Long applyId){
            matchingService.saveMatching(applyId, authorizationHeader);
            return CommonApiResponse.createSuccess();
    }

    //신청 거절
    @Operation(summary = "멘티 신청 거절, 수락과 통합 예정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "거절 성공"),
            @ApiResponse(responseCode = "fail", description = "거절 실패")})
    @PostMapping("/api/reject/{applyId}")
    public CommonApiResponse<?> boardDeny(@RequestHeader("Authorization") String authorizationHeader,
                                          @PathVariable Long applyId){
            matchingService.declineMatching(applyId, authorizationHeader);
            return CommonApiResponse.createSuccess();
    }

    //신청 글 조회
    @Operation(summary = "신청할 때 쓴 글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "지원 글 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "지원 글 조회 실패")})
    @GetMapping("/api/apply/{applyId}")
    public CommonApiResponse<ApplyInfoResponse> applyInfo(@RequestHeader("Authorization") String authorizationHeader,
                                                          @PathVariable Long applyId){
        Apply apply = applyService.findApplyById(applyId);
        ApplyValidation.isCheckContainMyApply(apply, memberService.findMemberByToken(authorizationHeader));
        return CommonApiResponse.createSuccess(ApplyInfoResponse.createApplyInfo(apply));
    }

    @Operation(summary = "신청 취소(삭제)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "삭제 성공"),
            @ApiResponse(responseCode = "fail", description = "삭제 실패")})
    @DeleteMapping("/api/apply/{applyId}")
    public CommonApiResponse<?> cancelApply(@PathVariable Long applyId,
                                            @RequestHeader("Authorization") String authorizationHeader){
        applyService.removeApply(applyId, authorizationHeader);
        return CommonApiResponse.createSuccess();
    }
}
