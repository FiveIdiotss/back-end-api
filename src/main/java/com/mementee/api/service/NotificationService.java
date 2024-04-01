package com.mementee.api.service;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.Notification;
import com.mementee.api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void save(Member member, String message){
        Notification notification = new Notification(message, member);
        notificationRepository.save(notification);
    }
}
