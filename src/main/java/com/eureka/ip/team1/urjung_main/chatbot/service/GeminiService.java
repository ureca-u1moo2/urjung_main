package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatbotRawResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ClassifiedTopicResult;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.chatbot.utils.gemini.GeminiResponseParser;
import com.eureka.ip.team1.urjung_main.chatbot.utils.gemini.GeminiResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.eureka.ip.team1.urjung_main.chatbot.utils.gemini.GeminiRequestFactory.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService implements ChatBotService {
    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final String geminiFullPath;


    // 토픽별 응답 처리
    @Override
    public Mono<ChatbotRawResponseDto> handleUserMessage(String prompt, String message, String recentChatHistory) {
        log.info(prompt);
        Map<String, Object> requestBody = buildChatBody(prompt, message, recentChatHistory);
        return sendChatRequest(requestBody)
                .map(GeminiResponseUtils::extractTextFromResponse)
                .map(GeminiResponseParser::toChatbotResponse);
    }

    // 토픽 분류
    @Override
    public Mono<ClassifiedTopicResult> classifyTopic(String message, String recentChatHistory) {
        Map<String, Object> requestBody = buildTopicClassifyBody(message, recentChatHistory);


        return sendChatRequest(requestBody)
                .map(GeminiResponseUtils::extractTextFromResponse)
                .map(GeminiResponseParser::toTopicResult)
                .onErrorResume(e -> Mono.just(new ClassifiedTopicResult(Topic.ETC, "잠시만 기다려주세요")));
    }

    // 성향 분석 질문에 대한 응답 확인
    @Override
    public Mono<ChatbotRawResponseDto> handleAnalysisAnswer(String prompt, String message) {
        Map<String, Object> requestBody = buildValidAnswerBody(prompt, message);
        return sendChatRequest(requestBody)
                .map(GeminiResponseUtils::extractTextFromResponse)
                .map(GeminiResponseParser::toChatbotResponse);
    }

    @Override
    public Mono<ChatbotRawResponseDto> requestRecommendationByAnalysis(String prompt) {
        Map<String, Object> requestBody = buildRecommendByAnalysisBody(prompt);
        return sendChatRequest(requestBody)
                .map(GeminiResponseUtils::extractTextFromResponse)
                .map(GeminiResponseParser::toChatbotResponse);
    }

    private Mono<Map> sendChatRequest(Map<String, Object> requestBody) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(geminiFullPath)
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class);
    }
}
