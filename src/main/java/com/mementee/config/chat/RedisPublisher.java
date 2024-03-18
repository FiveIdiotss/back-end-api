package com.mementee.config.chat;

import com.mementee.api.dto.redisDTO.RedisMessageSaveDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, RedisMessageSaveDTO message) {
        log.info("Redis Publisher");

        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}