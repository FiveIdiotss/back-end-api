package com.mementee.config.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mementee.api.domain.Notification;
import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import com.mementee.api.dto.notificationDTO.NotificationDTO;
import com.mementee.api.dto.redisDTO.RedisMessageSaveDTO;
import com.mementee.api.repository.NotificationRepository;
import com.mementee.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationService notificationService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = redisTemplate.getStringSerializer().deserialize(message.getBody());
        log.info("Subscribe");
        try {
            NotificationDTO notificationDTO = objectMapper.readValue(messageBody, NotificationDTO.class);
            Long receiverId = notificationDTO.getReceiverId();


            notificationService.sendToClient(receiverId, notificationDTO);
            log.info(messageBody);
        } catch (IOException e) {
            log.error("Error processing Redis message: {}", e.getMessage());
        }
    }
}