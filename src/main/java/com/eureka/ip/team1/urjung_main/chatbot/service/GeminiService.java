package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.common.exception.ChatBotException;
import com.eureka.ip.team1.urjung_main.common.exception.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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
    public Flux<ChatResponseDto> handleUserMessage(String userId, ChatRequestDto chatRequestDto) {
        log.info(chatRequestDto.getMessage());
        Map<String, Object> requestBody = buildChatRequestBody(chatRequestDto);

        // WebClient 호출
        try {
            Mono<Map> response = sendChatRequest(requestBody);

            return Flux.concat(
                    Flux.just(ChatResponseDto.builder()
                            .message("응답을 생성중입니다")
                            .build()),
                    response.map(this::extractReply)
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }

    private ChatResponseDto extractReply(Map<String, Object> response) {
        List<Map> candidates = (List<Map>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            log.error("gemini 응답 없음");
            throw new ChatBotException();
        }

        Map content = (Map) candidates.get(0).get("content");
        List<Map> parts = (List<Map>) content.get("parts");
        String reply = (String) parts.get(0).get("text");

        return ChatResponseDto.builder()
                .message(reply)
                .build();
    }

    private Map<String, Object> buildChatRequestBody(ChatRequestDto chatRequestDto) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", chatRequestDto.getMessage())
                                )
                        )
                )
        );
        return requestBody;
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
