package com.team.mementee.api.interceptor;

import com.team.mementee.api.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        // accessor stomp메시지의 헤더 정보에 접근할 수 있도록 도와주는 유틸리티 클래스
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null) {

            // 웹소켓 CONNECT 시점에 특정 해더 정보에서 읽어온 chatRoomId에 messsage sender를 입장시킴.
            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                String chatRoomIdHeader = accessor.getFirstNativeHeader("chatRoomId");
                String senderIdHeader = accessor.getFirstNativeHeader("senderId");

                log.info("SUBSCRIBED");
                // 헤더 정보로 채팅방 아이디, 전송자 아이디가 넘어왔을 때 유저를 채팅방에 입장시킴. (enterChatRoom)
                if (chatRoomIdHeader != null && senderIdHeader != null) {
                    long chatRoomId = Long.parseLong(chatRoomIdHeader);
                    long senderId = Long.parseLong(senderIdHeader);

                    accessor.getSessionAttributes().put("chatRoomId", chatRoomId);
                    accessor.getSessionAttributes().put("senderId", senderId);

                    chatService.userEnterChatRoom(chatRoomId, senderId);
                }
            }

            if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand()) || StompCommand.DISCONNECT.equals(accessor.getCommand())) {
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
