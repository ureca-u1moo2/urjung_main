package com.eureka.ip.team1.urjung_main.chatbot.handler;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import reactor.core.publisher.Flux;

public interface ChatStateHandler {
    ChatState getState();
    Flux<ChatResponseDto> handle(String userId, ChatRequestDto requestDto);
}
