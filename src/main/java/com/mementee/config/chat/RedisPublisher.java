package com.mementee.config.chat;

import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.dto.chatDTO.ChatMessageDTO;
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

// <<<<<<< final
//     public void publish(ChannelTopic channel, ChatMessageDTO message) {
//         log.info("Redis Publisher");

//         redisTemplate.convertAndSend(channel.getTopic(), message);
// =======
    public void publish(ChannelTopic topic, Object message) {
        log.info("Publishing to Redis topic: {}", topic.getTopic());
        try {
            redisTemplate.convertAndSend(topic.getTopic(), message);
        } catch (Exception e) {
            log.error("Error publishing message to Redis: {}", e.getMessage());
        }
    }
}