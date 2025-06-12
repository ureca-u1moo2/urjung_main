package com.eureka.ip.team1.urjung_main.chatbot.utils.gemini;

import com.eureka.ip.team1.urjung_main.chatbot.constants.GeminiConstants;
import com.eureka.ip.team1.urjung_main.common.exception.ChatBotException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class GeminiResponseUtils {
    public static String extractTextFromResponse(Map<String, Object> response) {
        List<Map> candidates = (List<Map>) response.get(GeminiConstants.CANDIDATES);
        if (candidates == null || candidates.isEmpty()) {
            log.error("candidates가 비어 있음");
            throw new ChatBotException();
        }

        Map content = (Map) candidates.get(0).get(GeminiConstants.CONTENT);
        List<Map> parts = (List<Map>) content.get(GeminiConstants.PARTS);
        return ((String) parts.get(0).get(GeminiConstants.TEXT)).trim();
    }
}
