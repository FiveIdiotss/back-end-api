package com.mementee.api.controller;

import com.mementee.api.domain.Apply;
import com.mementee.api.dto.applyDTO.*;
import com.mementee.api.dto.PageInfo;
import com.mementee.api.service.ApplyService;
import com.mementee.api.service.MatchingService;
import com.mementee.api.validation.ApplyValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.mementee.api.domain.enumtype.SendReceive;
import com.mementee.api.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Operation(description = "내가 신청 한/받은 글 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/myApply")
    public ResponseEntity<List<?>> applyList(@RequestHeader("Authorization") String authorizationHeader,
                                             @RequestParam SendReceive sendReceive){
        List<Apply> list = applyService.findMyApply(memberService.findMemberByToken(authorizationHeader), sendReceive);
        if(sendReceive == SendReceive.RECEIVE) {
            return ResponseEntity.ok(ApplyDTO.createReceiveApplyDTOs(list));
        }
        return ResponseEntity.ok(ApplyDTO.createSendApplyDTOs(list));
    }

    @Operation(description = "page 를 통한 내가 신청 한/받은 글 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/pageMyApply")
    public ResponseEntity<?> pageApplyList(@RequestHeader("Authorization") String authorizationHeader, @RequestParam SendReceive sendReceive,
                                        @RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Page<Apply> findApplies = applyService.findMyApplyByPage(memberService.findMemberByToken(authorizationHeader), sendReceive, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)findApplies.getTotalElements(), findApplies.getTotalPages());
        List<Apply> response = findApplies.getContent();

        if(sendReceive == SendReceive.RECEIVE) {
            List<ApplyDTO> list = ApplyDTO.createReceiveApplyDTOs(response);
            return new ResponseEntity<>(new PaginationApplyResponse(list, pageInfo), HttpStatus.OK);
        }
        List<ApplyDTO> list = ApplyDTO.createSendApplyDTOs(response);
        return new ResponseEntity<>(new PaginationApplyResponse(list, pageInfo), HttpStatus.OK);
    }

    //신청 받기
    @Operation(description = "멘토/멘티 신청 수락")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "신청 성공"),
            @ApiResponse(responseCode = "fail", description = "신청 실패")})
    @PostMapping("/api/apply/{applyId}")
    public ResponseEntity<String> boardApply(@PathVariable Long applyId,
                                             @RequestHeader("Authorization") String authorizationHeader){
        try {
            matchingService.saveMatching(applyId, authorizationHeader);
            return ResponseEntity.ok("신청 수락 성공");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("신청 수락 실패");
        }
    }

    //신청 거절
    @Operation(description = "멘토/멘티 신청 거절")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "거절 성공"),
            @ApiResponse(responseCode = "fail", description = "거절 실패")})
    @PostMapping("/api/reject/{applyId}")
    public ResponseEntity<String> boardDeny(@PathVariable Long applyId, @RequestHeader("Authorization") String authorizationHeader){
        try {
            matchingService.declineMatching(applyId, authorizationHeader);
            return ResponseEntity.ok("신청 거절 성공");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("신청 거절 실패");
        }
    }

    //신청 글 조회
    @Operation(description = "신청할때 쓴 글 조회 - (여기서 신청한 글로 이동할 수 있게)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "지원 글 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "지원 글 조회 실패")})
    @GetMapping("/api/apply/{applyId}")
    public ResponseEntity<ApplyInfoResponse> applyInfo(@RequestHeader("Authorization") String authorizationHeader,
                                                       @PathVariable Long applyId){
        Apply apply = applyService.findApplyById(applyId);
        ApplyValidation.isCheckContainMyApply(apply, memberService.findMemberByToken(authorizationHeader));
        return ResponseEntity.ok(ApplyInfoResponse.createApplyInfo(apply));
    }
}
