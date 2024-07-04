package com.team.mementee.api.repository.fcm;

import com.team.mementee.api.domain.FcmToken;
import com.team.mementee.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findFcmTokenByMember(Member member);

}
