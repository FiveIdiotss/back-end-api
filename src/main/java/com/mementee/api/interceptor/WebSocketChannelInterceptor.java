package com.mementee.api.interceptor;

import com.mementee.api.service.ChatService;
import com.mementee.api.service.RedisService;
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
            System.out.println(accessor);
            // 웹소켓 CONNECT 시점에 특정 해더 정보에서 읽어온 chatRoomId에 messsage sender를 입장시킴.
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                String chatRoomIdStr = accessor.getNativeHeader("chatRoomId").get(0);
                String senderIdStr = accessor.getNativeHeader("senderId").get(0);

                if (chatRoomIdStr == null || senderIdStr == null) throw new HeaderNotFound();

                Long chatRoomId = Long.parseLong(chatRoomIdStr);
                Long senderId = Long.parseLong(senderIdStr);

                // chatRoomId와 senderId를 websock DISCONNECT 시점에 재사용하기 위해 세션에 저장.
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
