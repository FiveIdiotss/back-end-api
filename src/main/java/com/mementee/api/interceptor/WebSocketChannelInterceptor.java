package com.mementee.api.interceptor;

import com.mementee.api.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final ChatService chatService;
    private final ApplicationContext applicationContext;
    //private final SimpMessagingTemplate websocketPublisher;

    private SimpMessagingTemplate getMessagingTemplate() {
        return applicationContext.getBean(SimpMessagingTemplate.class);
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // accessor stomp메시지의 헤더 정보에 접근할 수 있도록 도와주는 유틸리티 클래스
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null) {
            System.out.println(accessor);
            // 웹소켓 CONNECT 시점에 특정 해더 정보에서 읽어온 chatRoomId에 messsage sender를 입장시킴.
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                List<String> chatRoomIdHeaders = accessor.getNativeHeader("chatRoomId");
                List<String> senderIdHeaders = accessor.getNativeHeader("senderId");

                if (chatRoomIdHeaders != null && !chatRoomIdHeaders.isEmpty() && senderIdHeaders != null && !senderIdHeaders.isEmpty()) {
                    String chatRoomIdStr = chatRoomIdHeaders.get(0);
                    String senderIdStr = senderIdHeaders.get(0);

                    Long chatRoomId = Long.parseLong(chatRoomIdStr);
                    Long senderId = Long.parseLong(senderIdStr);

                    // chatRoomId와 senderId를 Websocket DISCONNECT 시점에 재사용하기 위해 세션에 저장.
                    accessor.getSessionAttributes().put("chatRoomId", chatRoomId);
                    accessor.getSessionAttributes().put("senderId", senderId);

                    chatService.userEnterChatRoom(chatRoomId, senderId);

                    Long numberOfUserInChatRoom = chatService.getNumberOfUserInChatRoom(chatRoomId);
                    if (numberOfUserInChatRoom == 2L) {
                        log.info("chatRoomId={}", chatRoomId);
                        getMessagingTemplate().convertAndSend("/sub/userCount/" + chatRoomId, numberOfUserInChatRoom.toString());
                    }

                } else {
                    // 헤더가 없을 경우 로그를 남기거나 다른 처리를 할 수 있습니다.
                    log.info("채팅방 목록 로딩 시점. CONNECT 시점에 chatRoomId 또는 senderId 헤더가 없습니다.");
                }
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
