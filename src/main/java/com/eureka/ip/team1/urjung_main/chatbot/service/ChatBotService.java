package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ClassifiedTopicResult;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatBotService {
    Mono<ChatResponseDto> handleUserMessage(String prompt, String message);
    Mono<ClassifiedTopicResult> classifyTopic(String prompt, String message);
}
