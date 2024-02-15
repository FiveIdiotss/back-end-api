package mementee.mementee.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.controller.applyDTO.AcceptRequest;
import mementee.mementee.api.controller.applyDTO.ApplyDTO;
import mementee.mementee.api.controller.applyDTO.ApplyInfo;
import mementee.mementee.api.controller.memberDTO.LoginMemberRequest;
import mementee.mementee.api.domain.Apply;
import mementee.mementee.api.domain.Member;
import mementee.mementee.api.service.ApplyService;
import mementee.mementee.api.service.MatchingService;
import mementee.mementee.api.service.MemberService;
import org.springframework.dao.EmptyResultDataAccessException;
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

    //신청 한 글인지 아닌지 체크하는 api 필요

    @Operation(description = "내가 신청한 글 리스트  - (현재 코드는 내가 신청 할때 쓴 글이 리스트로 보이게)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/sendApply/{memberId}")
    public List<ApplyDTO> mySendApplyList(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long memberId){
        memberService.isCheckMe(authorizationHeader, memberId);

        List<Apply> list = applyService.findApplyBySendMember(memberService.getMemberByToken(authorizationHeader).getId());
        List<ApplyDTO> collect = list.stream()
                .map(a -> new ApplyDTO(a.getId(), a.getBoard().getId(), a.getContent()))
                .toList();
        return collect;
    }

    @Operation(description = "내가 신청 받은 글 리스트 - (현재 코드는 내가 신청 할때 쓴 글이 리스트로 보이게)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/receiveApply/{memberId}")
    public List<ApplyDTO> myReceiveApplyList(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long memberId){
        memberService.isCheckMe(authorizationHeader , memberId);

        List<Apply> list = applyService.findApplyByReceiveMember(memberService.getMemberByToken(authorizationHeader).getId());
        List<ApplyDTO> collect = list.stream()
                .map(a -> new ApplyDTO(a.getId(), a.getBoard().getId(), a.getContent()))
                .toList();
        return collect;
    }

    //신청 받기
    @Operation(description = "멘토/멘티 신청 수락")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "신청 성공"),
            @ApiResponse(responseCode = "fail", description = "신청 실패")})
    @PostMapping("/api/apply/{applyId}")
    public ResponseEntity<String> boardApply(@Valid @RequestBody AcceptRequest request, @PathVariable Long applyId,
                                             @RequestHeader("Authorization") String authorizationHeader){
        try {
            matchingService.saveMatching(request, applyId, authorizationHeader);

            return ResponseEntity.ok("신청 수락 성공");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("신청 수락 실패");
        }
    }


    //신청한 글 조회
    @Operation(description = "신청 글 조회 - (여기서 신청한 글로 이동할 수 있게)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "지원 글 조회 성공"),
            @ApiResponse(responseCode = "fail", description = "지원 글 조회 실패")})
    @GetMapping("/api/apply/{applyId}")
    public ResponseEntity<?> applyInfo(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long applyId){
        try {
            Apply apply = applyService.isCheckMyApply(authorizationHeader, applyId);

            ApplyInfo response = new ApplyInfo(new ApplyDTO(apply.getId(), apply.getBoard().getId(),
                    apply.getContent()));
            return ResponseEntity.ok(response);

        }catch (EmptyResultDataAccessException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("지원 글 조회 실패");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("지원 글 조회 실패");
        }
    }
}
