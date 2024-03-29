package com.mementee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")     // 엔드포인트: /ws
                .setAllowedOrigins("*");
    }


    // websocket으로 전송할 수 있는 메시지 크기 설정.
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(2000000); // 메시지 크기 제한을 2MB로 설정합니다.
        registry.setSendTimeLimit(20 * 10000); // 20초의 타임아웃을 설정합니다.
        registry.setSendBufferSizeLimit(3 * 512 * 1024); // 버퍼 크기 제한을 설정합니다.
    }
}
