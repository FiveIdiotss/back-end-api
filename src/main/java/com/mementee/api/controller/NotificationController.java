package com.mementee.api.controller;

import com.mementee.api.domain.Notification;
import com.mementee.api.domain.Member;
import com.mementee.api.dto.CommonApiResponse;
import com.mementee.api.dto.PageInfo;
import com.mementee.api.dto.notificationDTO.NotificationDTO;
import com.mementee.api.dto.notificationDTO.PaginationFcmResponse;
import com.mementee.api.service.FcmService;
import com.mementee.api.service.MemberService;
import com.mementee.api.service.NotificationService;
import com.mementee.api.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

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


    @Operation(summary = "알림 갯수 Return")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "프로필 변경 성공"),
            @ApiResponse(responseCode = "fail", description = "프로필 변경 실패")})
    @GetMapping("/api/count")
    public CommonApiResponse<?> getNotificationCount(@RequestHeader("Authorization") String authorizationHeader) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        int unReadCount = redisService.getUnreadCount(member.getId());
        return CommonApiResponse.createSuccess(unReadCount);
    }

    @Operation(summary = "자신의 FCM 토큰 DB에 저장")
    @PostMapping("/api/fcm")
    public CommonApiResponse<?> saveFCMToken(@RequestHeader("Authorization") String authorizationHeader,
                                             @RequestParam String myToken){
        Member member = memberService.findMemberByToken(authorizationHeader);
        fcmService.saveFCMToken(member, myToken);
        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "알림 목록")
    @GetMapping("/api/push")
    public CommonApiResponse<PaginationFcmResponse> notificationList(@RequestParam int page, @RequestParam int size,
                                                                     @RequestHeader("Authorization") String authorizationHeader){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차 순(최신순)
        Page<Notification> fcms = notificationService.findNotificationsByReceiveMember(authorizationHeader, pageable);
        PageInfo pageInfo = new PageInfo(page, size, (int)fcms.getTotalElements(), fcms.getTotalPages());

        List<Notification> response = fcms.getContent();
        List<NotificationDTO> list = NotificationDTO.createNotificationDTOs(response);
        return CommonApiResponse.createSuccess(new PaginationFcmResponse(list, pageInfo));
    }

    @Operation(summary = "알림 삭제")
    @DeleteMapping("/api/push/{notificationId}")
    public CommonApiResponse<?> deleteNotification(@RequestHeader("Authorization") String authorizationHeader,
                                                   @PathVariable Long notificationId){
        notificationService.deleteNotification(authorizationHeader, notificationId);
        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "알림 받을 상대방 ID 입력 (테스트용)")
    @PostMapping("/api/push/fcm")
    public CommonApiResponse<?> pushMessage(@RequestParam Long targetMemberId){
        notificationService.sendNotification(targetMemberId);
        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "알림 리셋 (테스트용)")
    @PostMapping("/api/push/reset")
    public CommonApiResponse<?> resetMessage(@RequestParam Long targetMemberId){
        redisService.resetUnreadCount(targetMemberId);
        return CommonApiResponse.createSuccess();
    }
}
