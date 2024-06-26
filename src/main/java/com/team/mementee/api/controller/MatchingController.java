package com.team.mementee.api.controller;

import com.team.mementee.api.domain.Matching;
import com.team.mementee.api.dto.CommonApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.team.mementee.api.dto.matchingDTO.MatchingDTO;
import com.team.mementee.api.domain.enumtype.BoardType;
import com.team.mementee.api.service.MatchingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "멘토/멘티 매칭 목록, 관리")
public class MatchingController {

    private final MatchingService matchingService;

    //신청목록
    @Operation(summary = "매칭목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/matching")
    public CommonApiResponse<List<MatchingDTO>> myMatchingList(@RequestHeader("Authorization") String authorizationHeader,
                                                               @RequestParam BoardType boardType) {
        List<Matching> list = matchingService.findMatchingsByMember(boardType, authorizationHeader);
        return CommonApiResponse.createSuccess(MatchingDTO.createMatchingDTOs(list, boardType));
    }
}

