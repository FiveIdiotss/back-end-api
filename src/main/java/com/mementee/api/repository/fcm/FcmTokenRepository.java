package com.mementee.api.repository.fcm;

import com.mementee.api.domain.FcmToken;
import com.mementee.api.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findFcmTokenByMember(Member member);

}
