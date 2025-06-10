package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatbotRawResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ClassifiedTopicResult;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.common.exception.ChatBotException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService implements ChatBotService {
    private static final String SYSTEM_PROMPT = """
            당신은 U+ 통신사 상담 챗봇 요플레입니다.
            사용자의 요금제 관련 문의에 귀엽지만 정확하게 존댓말로 응답하세요.
            항상 명확하고 간결한 답변을 출력하세요. 이모지는 꼭 필요하다면 중요한 곳 한두군데에만 쓰세요.
            
            응답은 항상 아래 형식의 JSON 평문으로 출력하세요.  :
            
            {
              "reply": "여기에 사용자의 질문에 대한 답변을 입력하세요.",
              "planIds": ["요금제ID1", "요금제ID2"]
            }
            
            planIds는 필요할 때만 포함하세요.
            """;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final String geminiFullPath;


    @Override
    public Mono<ChatbotRawResponseDto> handleUserMessage(String prompt, String message) {
        Map<String, Object> requestBody = buildChatRequestBody(prompt, message);
        return sendChatRequest(requestBody)
                .map(this::extractRawChatBotResponse);
    }

    @Override
    public Mono<ClassifiedTopicResult> classifyTopic(String prompt, String message) {
        Map<String, Object> requestBody = buildTopicClassifyRequestBody(prompt, message);

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

    private Map<String, Object> buildTopicClassifyRequestBody(String systemPrompt, String userMessage) {
        Map<String, Object> systemInstruction = Map.of(
                "role", "system",
                "parts", List.of(Map.of("text", systemPrompt + "\n\n"))
        );

        Map<String, Object> userContent = Map.of(
                "role", "user",
                "parts", List.of(
                        Map.of("text",  userMessage)
                )
        );

        return Map.of(
                "systemInstruction", systemInstruction,
                "contents", List.of(userContent)
        );
    }


    private Map<String, Object> buildChatRequestBody(String subPrompt, String userMessage) {
        Map<String, Object> systemInstruction = Map.of(
                "role", "system",
                "parts", List.of(Map.of("text", SYSTEM_PROMPT + "\n\n"))
        );

        Map<String, Object> userContent = Map.of(
                "role", "user",
                "parts", List.of(
                        Map.of("text", subPrompt + "\n\n" + userMessage)
                )
        );

        return Map.of(
                "systemInstruction", systemInstruction,
                "contents", List.of(userContent)
        );
    }

    private ChatbotRawResponseDto extractRawChatBotResponse(Map<String, Object> response) {
        String raw = extractTextFromResponse(response);
        log.info(raw);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(extractPureJson(raw));

            String reply = root.has("reply") ? root.get("reply").asText() : raw;

            List<String> planIds = new ArrayList<>();
            if (root.has("planIds") && root.get("planIds").isArray()) {
                for (JsonNode idNode : root.get("planIds")) {
                    planIds.add(idNode.asText());
                }
            }

            return ChatbotRawResponseDto.builder()
                    .reply(reply)
                    .planIds(planIds.isEmpty() ? null : planIds)
                    .build();

        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new ChatBotException();
        }
    }

    private String extractPureJson(String rawResponse) {
        int start = rawResponse.indexOf("{");
        int end = rawResponse.lastIndexOf("}");
        if (start != -1 && end != -1 && start < end) {
            return rawResponse.substring(start, end + 1);
        }
        throw new IllegalArgumentException("JSON 형식이 잘못되었습니다.");
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
