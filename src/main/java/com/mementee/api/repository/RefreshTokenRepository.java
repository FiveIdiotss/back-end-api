package com.mementee.api.repository;

import com.mementee.api.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository <RefreshToken, Long> {

    Optional<RefreshToken> findRefreshTokenByEmail(String email);
    Optional<RefreshToken> findRefreshTokenByRefreshToken(String refreshToken);
}
