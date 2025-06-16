package com.eureka.ip.team1.urjung_main.chatbot.config;

import com.eureka.ip.team1.urjung_main.chatbot.entity.UserChatState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, UserChatState> userChatStateRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<UserChatState> serializer = new Jackson2JsonRedisSerializer<>(UserChatState.class);
        RedisSerializationContext<String, UserChatState> context = RedisSerializationContext
                .<String, UserChatState>newSerializationContext(new StringRedisSerializer())
                .value(serializer)
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
