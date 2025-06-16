package com.eureka.ip.team1.urjung_main.chatbot.utils.gemini;

import java.util.List;
import java.util.Map;

import static com.eureka.ip.team1.urjung_main.chatbot.constants.AIPromptMessages.SYSTEM_PROMPT;
import static com.eureka.ip.team1.urjung_main.chatbot.constants.AIPromptMessages.getTopicClassifyPrompt;
import static com.eureka.ip.team1.urjung_main.chatbot.constants.GeminiConstants.*;

public class GeminiRequestFactory {
    public static Map<String, Object> buildChatBody(String topicPrompt, String message, String recentChatHistory) {
        return Map.of(
                SYSTEM_INSTRUCTION, createSystem(SYSTEM_PROMPT + "\n" + topicPrompt),
                CONTENTS, List.of(createUserContent("이것은 최근 대화 기록 입니다 : " + recentChatHistory)
                        , createUserContent("\n이것은 현재 사용자의 메세지입니다" + message))
        );
    }

    public static Map<String, Object> buildAnalysis(String prompt, String message) {
        return Map.of(
                SYSTEM_INSTRUCTION, createSystem( prompt),
                CONTENTS, List.of(createUserContent("\n이것은 현재 사용자의 메세지입니다" + message))
        );
    }

    public static Map<String, Object> buildTopicClassifyBody(String message, String recentChatHistory) {
        return Map.of(
                SYSTEM_INSTRUCTION, createSystem(getTopicClassifyPrompt()),
                CONTENTS, List.of(createUserContent("이것은 최근 대화 기록 입니다 : " + recentChatHistory)
                        , createUserContent("\n이것은 현재 사용자의 메세지입니다" + message))
        );
    }

    private static Map<String, Object> createSystem(String text) {
        return Map.of(ROLE, ROLE_SYSTEM, PARTS, List.of(Map.of(TEXT, text)));
    }

    private static Map<String, Object> createUserContent(String text) {
        return Map.of(ROLE, ROLE_USER, PARTS, List.of(Map.of(TEXT, text)));
    }
}
