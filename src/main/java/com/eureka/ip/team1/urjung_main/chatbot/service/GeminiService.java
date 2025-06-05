package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.common.exception.ChatBotException;
import com.eureka.ip.team1.urjung_main.common.exception.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class GeminiService implements ChatBotService {
    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String url;
    private final WebClient webClient;

    public GeminiService() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public ChatResponseDto handleUserMessage(String userId, ChatRequestDto chatRequestDto) {
        log.info(chatRequestDto.getMessage());
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

        // WebClient 호출
        try {
            Map response = webClient.post()
                    .uri(url + "?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // 응답 파싱
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

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException();
        }

    }
}
