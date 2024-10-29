package com.team.mementee.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class BlackListTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration BLACKLIST_EXPIRATION = Duration.ofHours(1); // 1시간 만료 설정

    // 로그아웃 시 accessToken을 블랙리스트에 추가
    public void addBlackList(String accessToken) {
        // Redis에 블랙리스트 토큰 저장, 만료 시간 1시간 설정
        redisTemplate.opsForValue().set(accessToken, "blacklisted", BLACKLIST_EXPIRATION);
    }

    public boolean isCheckBlackList(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(accessToken));
    }
}
