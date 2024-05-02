package com.mementee.api.repository;

import com.mementee.api.domain.FCMNotification;
import com.mementee.api.domain.Favorite;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class FCMNotificationRepository {

    private final EntityManager em;

    public void save(FCMNotification fcmNotification) {
        em.persist(fcmNotification);
    }

    public Optional<FCMNotification> findFCMNotification(Long memberId){
        try {
            FCMNotification fcmNotification = em.createQuery("select f from FCMNotification f where f.member.id = :memberId" , FCMNotification.class)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
            return Optional.ofNullable(fcmNotification);
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

    public Optional<FCMNotification> findFCMNotificationByMemberId(Long memberId) {
        try {
            FCMNotification fcmNotification = em.createQuery("select f from FCMNotification f where f.member.id = : memberId ", FCMNotification.class)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
            return Optional.ofNullable(fcmNotification);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
