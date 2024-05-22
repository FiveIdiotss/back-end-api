package com.mementee.api.controller;

import com.mementee.api.domain.Matching;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.mementee.api.dto.matchingDTO.MatchingDTO;
import com.mementee.api.domain.enumtype.BoardType;
import com.mementee.api.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "멘토/멘티 매칭 목록, 관리")
public class MatchingController {

    private final MatchingService matchingService;

    //신청목록
    @Operation(description = "멘토/멘티 매칭목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @GetMapping("/api/matching")
    public ResponseEntity<List<MatchingDTO>> myMatchingList(@RequestHeader("Authorization") String authorizationHeader,
                                                            @RequestParam BoardType boardType) {
        List<Matching> list = matchingService.findMatchingByMentorOrMentee(boardType, authorizationHeader);
        if (boardType == BoardType.MENTOR) {
        return ResponseEntity.ok(list.stream()
                .map(m -> new MatchingDTO(m.getId(), m.getApply().getId(),
                        m.getMentor().getId(), m.getMentor().getName(),
                        m.getDate(), m.getStartTime()))
                .toList());
        }
        return ResponseEntity.ok(list.stream()
                .map(m -> new MatchingDTO(m.getId(), m.getApply().getId(),
                        m.getMentee().getId(), m.getMentee().getName(),
                        m.getDate(), m.getStartTime()))
                                .toList());
    }
}

