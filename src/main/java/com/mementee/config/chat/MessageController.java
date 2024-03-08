package com.mementee.config.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate template;
    private final RedisTemplate<String, Object> redisTemplate;

//    @MessageMapping("/hello")
//    public void sendMessage(final String message) {
//        System.out.println("메시지가 도착했습니다: " + message);
//        template.convertAndSend("/sub/chats/52" , message);
//
//        //redis에 저장
//        redisTemplate.convertAndSend(message, message);
//    }
}
