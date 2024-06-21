package com.team.mementee.api.repository;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository <RefreshToken, Long> {

    Optional<RefreshToken> findRefreshTokenByMember(Member member);

    Optional<RefreshToken> findRefreshTokenByRefreshToken(String refreshToken);
}
