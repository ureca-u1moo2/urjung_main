package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.entity.UserChatState;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
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
                .defaultIfEmpty(ChatState.IDLE);
    }

    @Override
    public Mono<ChatState> setState(String sessionId, ChatState state) {
        UserChatState userState = new UserChatState(sessionId, state);
        log.info("✅ setstate 들어옴: {}, {}", redisKey(sessionId),state.toString());
        return redisTemplate.opsForValue()
                .set(redisKey(sessionId), userState)
                .flatMap(success -> {
                    if (success) {
                        log.info("✅ Redis 저장 성공: {}", redisKey(sessionId));
                    } else {
                        log.warn("❌ Redis 저장 실패: {}", redisKey(sessionId));
                    }
                    return Mono.just(state);
                });
    }
}
