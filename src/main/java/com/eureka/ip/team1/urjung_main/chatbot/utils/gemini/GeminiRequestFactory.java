package com.eureka.ip.team1.urjung_main.chatbot.utils.gemini;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.GeminiConstants.*;
import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.TopicClassificationPrompts.getTopicClassifyPrompt;
import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.TopicPrompts.TOPIC_BASED_PROMPT;

public class GeminiRequestFactory {
    private static final Map<String, Object> thinkingConfig = Map.of(
            "thinkingBudget", 512
    );

    public static final Map<String, Object> generationReplyWithPlanIdsConfig = Map.of(
            "thinkingConfig", thinkingConfig,
            "responseMimeType", "application/json",
            "responseSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "reply", Map.of("type", "string"),
                            "planIds", Map.of(
                                    "type", "array",
                                    "items", Map.of("type", "string")
                            )
                    ),
                    "required", List.of("reply"),
                    "propertyOrdering", List.of("reply", "planIds")
            )
    );

    // 🧠 [topic + message] 구조 (토픽 분류 응답 등)
    public static final Map<String, Object> generationTopicMessageConfig = Map.of(
            "thinkingConfig", thinkingConfig,
            "responseMimeType", "application/json",
            "responseSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "topic", Map.of(
                                    "type", "string",
                                    "enum", Arrays.stream(Topic.values())
                                            .map(Enum::name)
                                            .collect(Collectors.toList())
                            ),
                            "message", Map.of("type", "string")
                    ),
                    "required", List.of("topic", "message"),
                    "propertyOrdering", List.of("topic", "message")
            )
    );

    // ✅ [reply + result] 구조 (성향 분석 답변 유효성 검증 등)
    public static final Map<String, Object> generationReplyWithResultConfig = Map.of(
            "thinkingConfig", thinkingConfig,
            "responseMimeType", "application/json",
            "responseSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "reply", Map.of("type", "string"),
                            "result", Map.of("type", "boolean")
                    ),
                    "required", List.of("reply", "result"),
                    "propertyOrdering", List.of("reply", "result")
            )
    );


    public static Map<String, Object> buildChatBody(String topicPrompt, String message, String recentChatHistory) {
        return Map.of(
                SYSTEM_INSTRUCTION, createSystem(TOPIC_BASED_PROMPT + "\n" + topicPrompt),
                CONTENTS, List.of(createUserContent("이것은 최근 대화 기록 입니다 : " + recentChatHistory)
                        , createUserContent("\n이것은 현재 사용자의 메세지입니다" + message)),
                GENERATION_CONFIG, generationReplyWithPlanIdsConfig
        );
    }

    public static Map<String, Object> buildValidAnswerBody(String prompt, String message) {
        return Map.of(
                SYSTEM_INSTRUCTION, createSystem(prompt),
                CONTENTS, List.of(createUserContent("\n이것은 현재 사용자의 메세지입니다" + message)),
                GENERATION_CONFIG, generationReplyWithResultConfig
        );
    }

    public static Map<String, Object> buildTopicClassifyBody(String message, String recentChatHistory) {
        return Map.of(
                SYSTEM_INSTRUCTION, createSystem(getTopicClassifyPrompt()),
                CONTENTS, List.of(createUserContent("이것은 최근 대화 기록 입니다 : " + recentChatHistory)
                        , createUserContent("\n이것은 현재 사용자의 메세지입니다" + message)),
                GENERATION_CONFIG, generationTopicMessageConfig
        );
    }

    public static Map<String, Object> buildRecommendByAnalysisBody(String prompt) {
        return Map.of(
                SYSTEM_INSTRUCTION, createSystem(prompt),
                CONTENTS, List.of(createUserContent("")),
                GENERATION_CONFIG, generationReplyWithPlanIdsConfig
        );
    }

    private static Map<String, Object> createSystem(String text) {
        return Map.of(ROLE, ROLE_SYSTEM, PARTS, List.of(Map.of(TEXT, text)));
    }

    private static Map<String, Object> createUserContent(String text) {
        return Map.of(ROLE, ROLE_USER, PARTS, List.of(Map.of(TEXT, text)));
    }
}
