package com.eureka.ip.team1.urjung_main.chatbot.utils.gemini;

import java.util.List;
import java.util.Map;

import static com.eureka.ip.team1.urjung_main.chatbot.constants.AIPromptMessages.SYSTEM_PROMPT;
import static com.eureka.ip.team1.urjung_main.chatbot.constants.AIPromptMessages.getTopicClassifyPrompt;
import static com.eureka.ip.team1.urjung_main.chatbot.constants.GeminiConstants.*;

public class GeminiRequestFactory {
    public static Map<String, Object> buildChatBody(String topicPrompt, String userMessage) {
        return Map.of(
                SYSTEM_INSTRUCTION, createSystem(SYSTEM_PROMPT),
                CONTENTS, List.of(createUserContent(topicPrompt + userMessage))
        );
    }

    public static Map<String, Object> buildTopicClassifyBody(String userMessage) {
        return Map.of(
                SYSTEM_INSTRUCTION, createSystem(getTopicClassifyPrompt()),
                CONTENTS, List.of(createUserContent(userMessage))
        );
    }

    private static Map<String, Object> createSystem(String text) {
        return Map.of(ROLE, ROLE_SYSTEM, PARTS, List.of(Map.of(TEXT, text)));
    }

    private static Map<String, Object> createUserContent(String text) {
        return Map.of(ROLE, ROLE_USER, PARTS, List.of(Map.of(TEXT, text)));
    }
}
