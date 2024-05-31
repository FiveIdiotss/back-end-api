package com.mementee.api.controller;

import com.mementee.api.domain.Notification;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.enumtype.NotificationType;
import com.mementee.api.dto.CommonApiResponse;
import com.mementee.api.dto.PageInfo;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.dto.notificationDTO.NotificationDTO;
import com.mementee.api.dto.notificationDTO.PaginationFcmResponse;
import com.mementee.api.service.FcmService;
import com.mementee.api.service.MemberService;
import com.mementee.api.service.NotificationService;
import com.mementee.api.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "push 알림/목록")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    private final FcmService fcmService;;
    private final MemberService memberService;
    private final RedisService redisService;


    @Operation(summary = "자신의 FCM 토큰 DB에 저장")
    @PostMapping("/api/fcm")
    public CommonApiResponse<?> saveFCMToken(@RequestHeader("Authorization") String authorizationHeader,
                                             @RequestParam String myToken){
        Member member = memberService.findMemberByToken(authorizationHeader);
        fcmService.saveFCMToken(member, myToken);
        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "알림 목록")
    @GetMapping("/api/fcm")
    public CommonApiResponse<PaginationFcmResponse> findFCMs(@RequestParam int page, @RequestParam int size,
                                                             @RequestHeader("Authorization") String authorizationHeader){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)
        Page<Notification> fcms = notificationService.findNotificationsByReceiveMember(authorizationHeader, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)fcms.getTotalElements(), fcms.getTotalPages());

        List<Notification> response = fcms.getContent();
        List<NotificationDTO> list = NotificationDTO.createNotificationDTOs(response);
        return CommonApiResponse.createSuccess(new PaginationFcmResponse(list, pageInfo));
    }

    @Operation(summary = "알림 받을 상대방 ID 입력 (테스트용)")
    @PostMapping("/api/push/fcm")
    public CommonApiResponse<?> pushMessage(@RequestParam Long targetMemberId){
        notificationService.sendNotification(targetMemberId);
        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "알림 리셋")
    @PostMapping("/api/push/reset")
    public CommonApiResponse<?> resetMessage(@RequestParam Long targetMemberId){
        redisService.resetUnreadCount(targetMemberId);
        return CommonApiResponse.createSuccess();
    }
}
