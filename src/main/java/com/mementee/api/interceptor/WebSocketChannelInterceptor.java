package com.mementee.api.interceptor;

import com.mementee.api.service.ChatService;
import com.mementee.exception.notFound.HeaderNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // accessor stomp메시지의 헤더 정보에 접근할 수 있도록 도와주는 유틸리티 클래스
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            // SUBSCRIBE 시점에 구독자를 채팅방에 입장시킴.
            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                // 헤더 정보가 제대로 안들어왔을 때 오류 처리 필요. [Failed to send message to MessageChannel] needs to be catched.
                String chatRoomIdStr = accessor.getFirstNativeHeader("chatRoomId");
                String senderIdStr = accessor.getFirstNativeHeader("senderId");

                if (chatRoomIdStr == null || senderIdStr == null) throw new HeaderNotFound();

                Long chatRoomId = Long.parseLong(chatRoomIdStr);
                Long senderId = Long.parseLong(senderIdStr);

                accessor.getSessionAttributes().put("chatRoomId", chatRoomId);
                accessor.getSessionAttributes().put("senderId", senderId);

                chatService.userEnterChatRoom(chatRoomId, senderId);
            }
            // 웹소켓 DISCONNECT 시점에 해당 채팅방에 입장했던 유저를 퇴장시킴.
            if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {

                Long chatRoomId = (Long) accessor.getSessionAttributes().get("chatRoomId");
                Long senderId = (Long) accessor.getSessionAttributes().get("senderId");

                if (chatRoomId != null && senderId != null) {
                    chatService.userLeaveChatRoom(chatRoomId, senderId);
                }
            }
        }

        return message;
    }

}
