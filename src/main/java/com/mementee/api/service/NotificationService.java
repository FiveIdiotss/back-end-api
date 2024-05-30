package com.mementee.api.service;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.Notification;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.repository.fcm.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberService memberService;


    public Page<Notification> findNotificationsByReceiveMember(String authorizationHeader, Pageable pageable){
        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        return notificationRepository.findNotificationsByReceiveMember(loginMember, pageable);
    }

    @Transactional
    public void saveNotification(FcmDTO fcmDTO) {
        Member targetMember = memberService.findMemberById(fcmDTO.getTargetMemberId());
        Member sendMember = memberService.findMemberById(fcmDTO.getSenderId());
        Notification notification = new Notification(fcmDTO.getContent(),
                fcmDTO.getNotificationType(), sendMember, targetMember);
        notificationRepository.save(notification);
    }
}
