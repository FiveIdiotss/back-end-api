package com.mementee.api.controller;

import com.mementee.api.domain.Apply;
import com.mementee.api.dto.applyDTO.*;
import com.mementee.api.dto.boardDTO.PageInfo;
import com.mementee.api.dto.boardDTO.PaginationResponseDto;
import com.mementee.api.service.ApplyService;
import com.mementee.api.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.mementee.api.domain.enumtype.SendReceive;
import com.mementee.api.service.MemberService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    @GetMapping("/api/myApply/{memberId}")
    public List<?> applyList(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long memberId,
                                                 @RequestParam SendReceive sendReceive){
        memberService.isCheckMe(authorizationHeader, memberId);

        List<Apply> list = applyService.findMyApply(memberService.getMemberByToken(authorizationHeader).getId(), sendReceive);
        if(sendReceive == SendReceive.RECEIVE) {
            return list.stream()
                    .map(a -> new ReceiveApplyDTO(a.getId(), a.getBoard().getId(), a.getContent(), a.getBoard().getTitle(), a.getApplyState(),
                            a.getSendMember().getId(), a.getSendMember().getName(),
                            a.getDate(), a.getStartTime(), a.getApplyTime()))
                    .toList();
        }

        return list.stream()
                .map(a -> new SendApplyDTO(a.getId(), a.getBoard().getId(), a.getContent(), a.getBoard().getTitle(), a.getApplyState(),
                        a.getReceiveMember().getId(), a.getReceiveMember().getName(),
                        a.getDate(), a.getStartTime(), a.getApplyTime()))
                .toList();
    }

    @Operation(description = "page 를 통한 내가 신청 한/받은 글 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/pageMyApply")
    public ResponseEntity pageApplyList(@RequestHeader("Authorization") String authorizationHeader,
                                        @RequestParam SendReceive sendReceive, @RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)

        Page<Apply> findApplies = applyService.findMyApplyByPage(memberService.getMemberByToken(authorizationHeader).getId(), sendReceive, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)findApplies.getTotalElements(), findApplies.getTotalPages());

        List<Apply> response = findApplies.getContent();
        if(sendReceive == SendReceive.RECEIVE) {
            List<ReceiveApplyDTO> list = response.stream().map
                    (a -> new ReceiveApplyDTO(a.getId(), a.getBoard().getId(), a.getContent(), a.getBoard().getTitle(), a.getApplyState(),
                            a.getSendMember().getId(), a.getSendMember().getName(),
                            a.getDate(), a.getStartTime(), a.getApplyTime()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(new PaginationReceiveApplyResponseDto(list, pageInfo), HttpStatus.OK);
        }

        List<SendApplyDTO> list = response.stream().map
                        (a -> new SendApplyDTO(a.getId(), a.getBoard().getId(), a.getContent(), a.getBoard().getTitle(), a.getApplyState(),
                                a.getReceiveMember().getId(), a.getReceiveMember().getName(),
                                a.getDate(), a.getStartTime(), a.getApplyTime()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(new PaginationSendApplyResponseDto(list, pageInfo), HttpStatus.OK);
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
    @Operation(description = "신청 글 조회 - (여기서 신청한 글로 이동할 수 있게)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "지원 글 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "지원 글 조회 실패")})
    @GetMapping("/api/apply/{applyId}")
    public ResponseEntity<?> applyInfo(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long applyId){
        try {
            applyService.isCheckApply(authorizationHeader, applyId);
            Apply apply = applyService.findApplication(applyId);

            ApplyInfo response = new ApplyInfo(apply.getId(), apply.getBoard().getId(),
                    apply.getContent(), apply.getBoard().getTitle(), apply.getApplyState(),
                    apply.getDate(), apply.getStartTime(), apply.getBoard().getMember().getId(),
                    apply.getBoard().getMember().getName(), apply.getBoard().getMember().getMemberImageUrl()
                    ,apply.getBoard().getMember().getMajor().getSchool().getName(),
                    apply.getBoard().getMember().getMajor().getName());

            return ResponseEntity.ok(response);
        }catch (EmptyResultDataAccessException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
