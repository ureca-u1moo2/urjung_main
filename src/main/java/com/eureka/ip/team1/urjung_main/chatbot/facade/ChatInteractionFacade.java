package com.eureka.ip.team1.urjung_main.chatbot.facade;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import reactor.core.publisher.Flux;

public interface ChatInteractionFacade {
    Flux<ChatResponseDto> chat(String id, ChatRequestDto chatRequestDto);

    Flux<ChatResponseDto> startRecommendationFlow(String userId, ChatRequestDto requestDto);

    Flux<ChatResponseDto> changeStateToDefault(String userId, ChatRequestDto requestDto);

    Flux<ChatResponseDto> changeStateToPersonalAnalysis(String userId, ChatRequestDto requestDto);
}
