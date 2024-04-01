package com.mementee.config.chat;

import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandConfig = new RedisStandaloneConfiguration();
        redisStandConfig.setHostName("menteetor.site");
        //redisStandConfig.setHostName("localhost");
        redisStandConfig.setPort(6379);
        return new LettuceConnectionFactory(redisStandConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessageDTO.class));
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }

//    @Bean
//    public RedisMessageListenerContainer redisMessageListenerContainer(
//            RedisConnectionFactory connectionFactory,
//            @Qualifier("redisSubscriber") RedisSubscriber redisSubscriber) {
//
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//
//        // 특정 채널을 RedisSubscriber에게 연결
//        container.addMessageListener(redisSubscriber, new ChannelTopic("chatRoom1"));
//
//        log.info("Redis Config");
//
//        return container;
//    }
}
