package com.mementee.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    //받는 사람
    //알림 발생할 때마다 추가
    public void incrementUnreadCount(Long memberId) {
        String key = getKey(memberId);
        redisTemplate.opsForValue().increment(key);
    }

    //읽었을 때 갯수 초기화
    public void resetUnreadCount(Long memberId) {
        String key = getKey(memberId);
        redisTemplate.opsForValue().set(key, 0);
    }

    //알림 갯수
    public Integer getUnreadCount(Long memberId) {
        String key = getKey(memberId);
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? (Integer) count : 0;
    }

    //채팅방에 대한 알림 key
    private String getKey(Long memberId) {
        return "notify:unread:" + memberId;
    }
}
