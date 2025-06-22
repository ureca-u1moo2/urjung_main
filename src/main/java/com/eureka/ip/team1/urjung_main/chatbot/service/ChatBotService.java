package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatbotRawResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ClassifiedTopicResult;
import reactor.core.publisher.Mono;

public interface ChatBotService {
    Mono<ChatbotRawResponseDto> handleUserMessage(String prompt, String message, String recentHistory);

    Mono<ClassifiedTopicResult> classifyTopic(String message, String recentHistory);

    Mono<ChatbotRawResponseDto> handleAnalysisAnswer(String prompt, String message);

    Mono<ChatbotRawResponseDto> requestRecommendationByAnalysis(String prompt);
}
