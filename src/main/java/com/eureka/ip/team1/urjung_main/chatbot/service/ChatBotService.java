package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import reactor.core.publisher.Flux;

public interface ChatBotService {
    Flux<ChatResponseDto> handleUserMessage(String userId, ChatRequestDto chatRequestDto);
}
