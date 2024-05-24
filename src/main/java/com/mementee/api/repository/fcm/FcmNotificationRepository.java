package com.mementee.api.repository.fcm;

import com.mementee.api.domain.FcmNotification;
import com.mementee.api.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmNotificationRepository extends JpaRepository<FcmNotification, Long> {

    Optional<FcmNotification> findFCMNotificationByMember(Member member);

    Page<FcmNotification> findFCMNotificationsByMember(Member member, Pageable pageable);

}
