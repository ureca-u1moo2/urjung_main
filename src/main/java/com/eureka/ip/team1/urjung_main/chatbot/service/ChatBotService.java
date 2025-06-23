package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatbotRawResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ClassifiedTopicResult;
import reactor.core.publisher.Mono;

public interface ChatBotService {
    Mono<ChatbotRawResponseDto> generateChatReply(String prompt, String message, String recentHistory);

    Mono<ClassifiedTopicResult> classifyUserIntent(String message, String recentHistory);

    Mono<ChatbotRawResponseDto> validateAnalysisAnswer(String prompt, String message);

    Mono<ChatbotRawResponseDto> generateFinalRecommendation(String prompt);
}
