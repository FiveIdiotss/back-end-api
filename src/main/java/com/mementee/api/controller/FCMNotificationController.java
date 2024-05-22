package com.mementee.api.controller;

import com.mementee.api.domain.FcmDetail;
import com.mementee.api.domain.Member;
import com.mementee.api.dto.PageInfo;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.dto.notificationDTO.PaginationFcmResponse;
import com.mementee.api.service.FcmNotificationService;
import com.mementee.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "push 알림 테스트 (안드로이드)")
@Slf4j
public class FCMNotificationController {

    private final FcmNotificationService fcmNotificationService;
    private final MemberService memberService;

    @Operation(description = "자신의 FCM 토큰 DB에 저장")
    @PostMapping("/api/fcm")
    public ResponseEntity<String> saveFCMToken(@RequestHeader("Authorization") String authorizationHeader,
                                               @RequestParam String myToken){
        Member member = memberService.findMemberByToken(authorizationHeader);
        fcmNotificationService.saveFCMNotification(member, myToken);
        return ResponseEntity.ok("저장 성공");
    }

    @Operation(description = "알림 목록")
    @GetMapping("/api/fcm")
    public ResponseEntity<PaginationFcmResponse> findFCMs(@RequestParam int page, @RequestParam int size,
                                                          @RequestHeader("Authorization") String authorizationHeader){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)
        Page<FcmDetail> fcms = fcmNotificationService.findFcmDetailsByReceiverMember(authorizationHeader, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)fcms.getTotalElements(), fcms.getTotalPages());

        List<FcmDetail> response = fcms.getContent();
        List<FcmDTO> list = FcmDTO.createFcmList(response);
        return new ResponseEntity<>(new PaginationFcmResponse(list, pageInfo), HttpStatus.OK);
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
