package com.mementee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketMessageBrokerConfiguration;

@Configuration
public class MessageConfig {
    @Bean
    public DelegatingWebSocketMessageBrokerConfiguration webSocketMessageBroker(){
        return new DelegatingWebSocketMessageBrokerConfiguration();
    }
}
