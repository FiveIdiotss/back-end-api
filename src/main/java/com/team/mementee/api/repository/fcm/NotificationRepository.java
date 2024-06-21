package com.team.mementee.api.repository.fcm;

import com.team.mementee.api.domain.Notification;
import com.team.mementee.api.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findNotificationsByReceiveMember(Member loginMember, Pageable pageable);

}
