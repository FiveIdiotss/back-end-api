//package com.mementee.config.chat;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.Resource;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class RedisSubscriber {
//
//    private final ObjectMapper objectMapper;
//
//    @Resource(name = "chatRedisTemplate")
//    private final RedisTemplate<String, Object> redisTemplate;
//    private SimpMessageSendingOperations messagingTemplate;
//
////    public void onMessage() {
////        redisTemplate.getStringSerializer().deserialize()
////    }
//
//}
