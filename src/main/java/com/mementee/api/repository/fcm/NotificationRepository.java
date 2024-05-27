package com.mementee.api.repository.fcm;

import com.mementee.api.domain.Notification;
import com.mementee.api.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findFcmDetailsByReceiveMember(Member loginMember, Pageable pageable);

}
