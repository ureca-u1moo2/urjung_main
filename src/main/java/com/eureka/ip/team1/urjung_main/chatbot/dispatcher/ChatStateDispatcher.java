package com.eureka.ip.team1.urjung_main.chatbot.dispatcher;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import reactor.core.publisher.Flux;

public interface ChatStateDispatcher {
    Flux<ChatResponseDto> dispatch(String userId, ChatRequestDto requestDto);
}
