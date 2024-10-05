package com.team.mementee.api.service;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.Notification;
import com.team.mementee.api.dto.notificationDTO.FcmDTO;
import com.team.mementee.api.repository.fcm.NotificationRepository;
import com.team.mementee.api.validation.MemberValidation;
import com.team.mementee.exception.notFound.NotificationNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    @Value("${websocket.notification-path}")
    private String websocketPath;

    @Value("${websocket.chat-notification-path}")
    private String totalChatCountPath;

    private final NotificationRepository notificationRepository;
    private final MemberService memberService;
    private final ChatService chatService;
    private final RedisService redisService;
    private final SimpMessagingTemplate websocketPublisher;

    public void sendTotalChatCount(Long targetMemberId) {
        int totalChatCount = getUnreadChatCount(targetMemberId);

        // WebSocket을 통해 실시간으로 클라이언트에 알림 개수 전송
        websocketPublisher.convertAndSend(totalChatCountPath + targetMemberId, totalChatCount);
    }

    public void sendNotification(Long targetMemberId) {

        // 알림 발생 시 Redis에 알림 개수 증가
        redisService.incrementUnreadCount(targetMemberId);

        // 현재 알림 개수 조회
        Integer unreadCount = redisService.getUnreadCount(targetMemberId);

        // WebSocket을 통해 실시간으로 클라이언트에 알림 개수 전송
        websocketPublisher.convertAndSend(websocketPath + targetMemberId, unreadCount);
    }

    public Notification findNotificationById(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isEmpty())
            throw new NotificationNotFound();
        return notification.get();
    }

    public Page<Notification> findNotificationsByReceiveMember(String authorizationHeader, Pageable pageable) {
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
        sendNotification(receiveMember.getId());
    }

    @Transactional
    public void deleteNotification(String authorizationHeader, Long notificationId) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        Notification notification = findNotificationById(notificationId);
        MemberValidation.isCheckMe(member, memberService.findMemberById(notification.getReceiveMember().getId()));

        notificationRepository.delete(notification);
    }

    public int getUnreadChatCount(Long targetMemberId) {
        return chatService.findAllChatRoomByMemberId(targetMemberId)
                .stream()
                .mapToInt(chatRoom -> chatService.countUnreadMessages(chatRoom.getId(), targetMemberId))
                .sum();

    }

}
