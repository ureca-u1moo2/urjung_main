package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import reactor.core.publisher.Mono;

public interface ChatStateService {
    Mono<ChatState> getState(String sessionId);
    Mono<ChatState> setState(String sessionId, ChatState state);
}