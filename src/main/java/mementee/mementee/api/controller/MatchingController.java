package mementee.mementee.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.controller.applyDTO.ApplyDTO;
import mementee.mementee.api.controller.matchingDTO.MatchingDTO;
import mementee.mementee.api.domain.Apply;
import mementee.mementee.api.domain.Matching;
import mementee.mementee.api.domain.enumtype.BoardType;
import mementee.mementee.api.service.MatchingService;
import mementee.mementee.api.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "멘토/멘티 매창 목록, 관리")
public class MatchingController {

    private final MatchingService matchingService;
    private final MemberService memberService;

    //신청목록
    @GetMapping("/api/matching/{memberId}")
    public List<MatchingDTO> mentorMatchingList(@RequestHeader("Authorization") String authorizationHeader, @RequestParam BoardType boardType,
                                                @PathVariable("memberId") Long memberId){
        memberService.isCheckMe(authorizationHeader, memberId);

        List<Matching> list = matchingService.findMyMatching(boardType, authorizationHeader);
        List<MatchingDTO> collect = list.stream()
                .map(m -> new MatchingDTO(m.getId(), m.getDate(), m.getStartTime()))
                .toList();
        return collect;
    }

}
