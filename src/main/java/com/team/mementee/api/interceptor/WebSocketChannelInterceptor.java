package com.team.mementee.api.interceptor;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.service.ChatService;
import com.team.mementee.api.service.MemberService;
import com.team.mementee.api.service.RedisService;
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
    private final MemberService memberService;
    private final RedisService redisService;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        // accessor stomp메시지의 헤더 정보에 접근할 수 있도록 도와주는 유틸리티 클래스
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null) {
            // 웹소켓 CONNECT 시점에 특정 해더 정보에서 읽어온 chatRoomId에 messsage sender를 입장시킴.
            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                String token = accessor.getFirstNativeHeader("Authorization");
                String chatRoomIdHeader = accessor.getFirstNativeHeader("chatRoomId");

                if (token != null && chatRoomIdHeader != null) {
                    Member member = memberService.findMemberByToken(token);
                    Long chatRoomId = Long.parseLong(chatRoomIdHeader);

//                    redisService.save("member" + member.getId(), member);
//                    redisService.save("chatRoomId" + chatRoomId, chatRoomId);

                    accessor.getSessionAttributes().put("member", member);
                    accessor.getSessionAttributes().put("chatRoomId", chatRoomId);

                    chatService.userEnterChatRoom(chatRoomId, member.getId());
                }
            }
        }

        if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand()) || StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            Long chatRoomId = (Long) accessor.getSessionAttributes().get("chatRoomId");
            Long senderId = (Long) accessor.getSessionAttributes().get("senderId");

            if (chatRoomId != null && senderId != null) {
                chatService.userLeaveChatRoom(chatRoomId, senderId);
            }
        }

        return message;
    }

}
