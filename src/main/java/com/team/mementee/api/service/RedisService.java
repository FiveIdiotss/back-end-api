package com.team.mementee.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> stringRedisTemplate; // String 타입으로 변경
    private final RedisTemplate<String, Object> redisTemplate; // String 타입으로 변경

    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 객체 불러오기
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key); // Redis에서 JSON 문자열을 가져옴
    }

    // 객체 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }



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
        return "notification:unreadCount:" + memberId;
    }
}
