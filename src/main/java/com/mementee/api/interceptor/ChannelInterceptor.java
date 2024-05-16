package com.mementee.api.interceptor;

import com.mementee.api.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;

@RequiredArgsConstructor
public class ChannelInterceptor implements org.springframework.messaging.support.ChannelInterceptor {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                Long userId = Long.parseLong(accessor.getFirstNativeHeader("userId"));
                Long chatRoomId = Long.parseLong(accessor.getFirstNativeHeader("chatRoomId"));
                chatService.userEnterChatRoom(chatRoomId, userId);

                // 현재 채팅방에 접속 중인 사용자들에게 최신 데이터를 요청하도록 알림
                chatService.getUsersInChatRoom(chatRoomId).forEach(otherUserId -> {
                    if (!otherUserId.equals(userId)) {
                        simpMessagingTemplate.convertAndSend("/sub/chats/" + chatRoomId + "/reload", otherUserId);
                    }
                });
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                Long userId = Long.parseLong(accessor.getFirstNativeHeader("userId"));
                Long chatRoomId = Long.parseLong(accessor.getFirstNativeHeader("chatRoomId"));
                chatService.userLeaveChatRoom(chatRoomId, userId);
            }
        }
        return message;
    }

}
