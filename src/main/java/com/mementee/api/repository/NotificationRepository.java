package com.mementee.api.repository;

import com.mementee.api.domain.chat.Notification;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    private final EntityManager em;
    public void save(Notification notification) {
        em.persist(notification);
    }

    public Notification findNotification(Long notificationId){
        return em.find(Notification.class, notificationId);
    }
}
