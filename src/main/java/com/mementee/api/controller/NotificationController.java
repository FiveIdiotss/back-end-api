package com.mementee.api.controller;

import com.mementee.api.service.MemberService;
import com.mementee.api.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/sse")
@Tag(name = "SSE 알림")
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberService memberService;

    @Operation(description = "구독 시 만료시간동안 발생한 알림 반환, 추 후 jwt 토큰으로 변경 예정")
    @GetMapping(value = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(//@RequestHeader("Authorization") String authorizationHeader,
                                                @PathVariable("memberId") Long memberId,
                                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        //Member member = memberService.getMemberByToken(authorizationHeader);
        return ResponseEntity.ok(notificationService.subscribe(memberId));
    }

    @Operation(description = "해당 memberId 에 알림 보내기 (테스트용)")
    @PostMapping("/send-data/{memberId}")
    public void sendData(@PathVariable Long memberId) {
        notificationService.notify(memberId, "data");
    }
}
