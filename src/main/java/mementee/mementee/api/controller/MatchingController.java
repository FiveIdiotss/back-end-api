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
import mementee.mementee.api.controller.matchingDTO.MatchingDTO;
import mementee.mementee.api.domain.Matching;
import mementee.mementee.api.domain.enumtype.BoardType;
import mementee.mementee.api.service.MatchingService;
import mementee.mementee.api.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "멘토/멘티 매칭 목록, 관리")
public class MatchingController {

    private final MatchingService matchingService;
    private final MemberService memberService;

    //신청목록
    @Operation(description = "멘토/멘티 매칭목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/matching/{memberId}")
    public List<MatchingDTO> myMatchingList(@RequestHeader("Authorization") String authorizationHeader, @RequestParam BoardType boardType,
                                            @PathVariable("memberId") Long memberId){
        memberService.isCheckMe(authorizationHeader, memberId);

        List<Matching> list = matchingService.findMyMatching(boardType, authorizationHeader);
        List<MatchingDTO> collect = list.stream()
                .map(m -> new MatchingDTO(m.getId(), m.getDate(), m.getStartTime()))
                .toList();
        return collect;
    }

}
