package com.mementee.api.controller;

import com.mementee.api.domain.Member;
import com.mementee.api.service.FCMNotificationService;
import com.mementee.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "push 알림 테스트 (안드로이드)")
@Slf4j
public class FCMNotificationController {

    private final FCMNotificationService fcmNotificationService;
    private final MemberService memberService;

    @Operation(description = "자신의 FCM 토큰 DB에 저장")
    @PostMapping("/api/fcm")
    public ResponseEntity<String> saveFCMToken(@RequestHeader("Authorization") String authorizationHeader,
                                               @RequestParam String myToken){
        Member member = memberService.findMemberByToken(authorizationHeader);
        fcmNotificationService.saveFCMNotification(member, myToken);
        return ResponseEntity.ok("저장 성공");
    }

//    @Operation(description = "알림 받을 상대방 ID 입력 (테스트용)")
//    @PostMapping("/api/push/fcm")
//    public ResponseEntity<String> pushMessage(@RequestHeader("Authorization") String authorizationHeader,
//                                              @RequestParam Long targetMemberId) throws IOException {
//        //fcmNotificationService.sendMessageTo(targetMemberId, "test", "test", authorizationHeader);
//        fcmNotificationService.sendMessageTo(targetMemberId, "testTitle", "testBody", "testSenderId","testUrl");
//        return ResponseEntity.ok().build();
//    }
}
