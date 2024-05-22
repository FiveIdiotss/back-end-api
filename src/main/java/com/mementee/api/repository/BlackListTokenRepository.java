package com.mementee.api.repository;

import com.mementee.api.domain.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListTokenRepository extends JpaRepository<BlackListToken, Long> {
    Optional<BlackListToken>findBlackListTokenByBlackListToken(String blackListToken);
}
