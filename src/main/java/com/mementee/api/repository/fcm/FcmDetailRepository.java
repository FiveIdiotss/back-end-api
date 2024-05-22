package com.mementee.api.repository.fcm;

import com.mementee.api.domain.FcmDetail;
import com.mementee.api.domain.FcmNotification;
import com.mementee.api.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmDetailRepository extends JpaRepository<FcmDetail, Long> {

    Page<FcmDetail> findFcmDetailsByReceiveMember(Member loginMember, Pageable pageable);

}
