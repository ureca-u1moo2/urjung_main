package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.entity.UserChatState;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatStateServiceImpl implements ChatStateService {
    private final ReactiveRedisTemplate<String, UserChatState> redisTemplate;

    private String redisKey(String sessionId) {
        return "chatstate:" + sessionId;
    }

    @Override
    public Mono<ChatState> getState(String sessionId) {
        return redisTemplate.opsForValue()
                .get(redisKey(sessionId))
                .map(UserChatState::getState)
                .defaultIfEmpty(ChatState.DEFAULT);
    }

    @Override
    public Mono<ChatState> setState(String sessionId, ChatState state) {
        return redisTemplate.opsForValue()
                .set(redisKey(sessionId), new UserChatState(sessionId, state, LocalDateTime.now()))
                .thenReturn(state);
    }

    @Override
    public Mono<Boolean> isInState(String userId, ChatState state) {
        return getState(userId).map(current -> current == state);
    }
}
