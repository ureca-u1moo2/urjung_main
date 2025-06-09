package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ClassifiedTopicResult;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.common.exception.ChatBotException;
import com.eureka.ip.team1.urjung_main.common.exception.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService implements ChatBotService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final String geminiFullPath;

    @Override
    public Mono<ChatResponseDto> handleUserMessage(String prompt, String message) {
        Map<String, Object> requestBody = buildChatRequestBody(prompt, message);
        return sendChatRequest(requestBody).map(this::extractReply);
    }

    @Override
    public Mono<ClassifiedTopicResult> classifyTopic(String prompt, String message) {
        Map<String, Object> requestBody = buildChatRequestBody(prompt,message);

        return sendChatRequest(requestBody)
                .map(this::extractClassifiedResult)
                .onErrorResume(e -> {
                    log.warn("토픽 분류 실패: {}", e.getMessage());
                    return Mono.just(new ClassifiedTopicResult(Topic.ETC, "잠시만 기다려주세요"));
                });
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

    private Map<String, Object> buildChatRequestBody(String prompt, String message) {
        Map<String, Object> systemInstruction = Map.of(
                "role", "system",
                "parts", List.of(Map.of("text", prompt))
        );

        Map<String, Object> userContent = Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", message))
        );

        return Map.of(
                "systemInstruction", systemInstruction,
                "contents", List.of(userContent)
        );
    }

    private ChatResponseDto extractReply(Map<String, Object> response) {
        String reply = extractTextFromResponse(response);
        return ChatResponseDto.builder()
                .message(reply)
                .build();
    }

    private ClassifiedTopicResult extractClassifiedResult(Map<String, Object> response) {
        String raw = extractTextFromResponse(response);
        log.info("Gemini 응답 (raw): {}", raw);

        String[] split = raw.split(":", 2);
        String topicStr = split[0].trim();
        String waitMessage = (split.length > 1) ? split[1].trim() : "";

        try {
            Topic topic = Topic.valueOf(topicStr);
            return new ClassifiedTopicResult(topic, waitMessage);
        } catch (IllegalArgumentException e) {
            throw new ChatBotException();
        }
    }

    private String extractTextFromResponse(Map<String, Object> response) {
        List<Map> candidates = (List<Map>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new ChatBotException();
        }

        Map content = (Map) candidates.get(0).get("content");
        List<Map> parts = (List<Map>) content.get("parts");
        return ((String) parts.get(0).get("text")).trim();
    }
}
