package com.mementee.api.service;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.Notification;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.repository.fcm.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberService memberService;
    private final RedisService redisService;

    @Lazy
    private final SimpMessagingTemplate websocketPublisher;

    public void sendNotification(Long targetMemberId) {

        // 알림 발생 시 Redis에 알림 개수 증가
        redisService.incrementUnreadCount(targetMemberId);

        // 현재 알림 개수 조회
        Integer unreadCount = redisService.getUnreadCount(targetMemberId);

        // WebSocket을 통해 실시간으로 클라이언트에 알림 개수 전송
        websocketPublisher.convertAndSend("/sub/notifications/" + targetMemberId, unreadCount);
    }

    public Page<Notification> findNotificationsByReceiveMember(String authorizationHeader, Pageable pageable){
        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        redisService.resetUnreadCount(loginMember.getId());
        return notificationRepository.findNotificationsByReceiveMember(loginMember, pageable);
    }

    @Transactional
    public void saveNotification(FcmDTO fcmDTO) {
        Member sendMember = memberService.findMemberById(fcmDTO.getSenderId());
        Member receiveMember = memberService.findMemberById(fcmDTO.getTargetMemberId());
        Notification notification = new Notification(fcmDTO.getTitle(), fcmDTO.getContent(), fcmDTO.getOtherPK(),
                fcmDTO.getNotificationType(), sendMember, receiveMember);
        notificationRepository.save(notification);
    }
}