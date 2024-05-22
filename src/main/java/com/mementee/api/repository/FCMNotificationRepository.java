package com.mementee.api.repository;

import com.mementee.api.domain.FCMNotification;
import com.mementee.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FCMNotificationRepository extends JpaRepository<FCMNotification, Long> {
    Optional<FCMNotification> findFCMNotificationByMember(Member member);

}
