package com.mementee.config.chat;

import com.mementee.api.controller.redisDTO.RedisMessageSaveDTO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, RedisMessageSaveDTO message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}