package com.eureka.ip.team1.urjung_main.chatbot.utils.gemini;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatbotRawResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ClassifiedTopicResult;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.common.exception.ChatBotException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.GeminiConstants.FIELD_PLAN_IDS;
import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.GeminiConstants.FIELD_REPLY;

@Slf4j
public class GeminiResponseParser {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ChatbotRawResponseDto toChatbotResponse(String rawJson) {
        try {
            log.info("rawJson : {}", rawJson);
            JsonNode root = mapper.readTree(extractPureJson(rawJson));
            String reply = root.has(FIELD_REPLY) ? root.get(FIELD_REPLY).asText() : rawJson;

            List<String> planIds = new ArrayList<>();
            if (root.has(FIELD_PLAN_IDS)) {
                root.get(FIELD_PLAN_IDS).forEach(n -> planIds.add(n.asText()));
            }

            Boolean needMyPlan = null;
            if (root.has("need_my_plan")) {
                needMyPlan = true;
            }

            Boolean result = null;
            if (root.has("result")) {
                result = root.get("result").asBoolean();  // true 또는 false
            }

            return ChatbotRawResponseDto.builder()
                    .reply(reply)
                    .planIds(planIds.isEmpty() ? null : planIds)
                    .result(result)
                    .needSelectLine(needMyPlan)
                    .build();
        } catch (Exception e) {
            log.error("챗봇 응답 파싱 실패: {}", e.getMessage());
            throw new ChatBotException();
        }
    }

    public static ClassifiedTopicResult toTopicResult(String raw) {
        log.info("topic raw : {}", raw);

        try {
            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(raw);

            String topicStr = node.get("topic").asText();
            String message = node.get("message").asText();

            Topic topic = Topic.valueOf(topicStr); // Enum 변환
            return new ClassifiedTopicResult(topic, message);

        } catch (Exception e) {
            log.error("토픽 파싱 실패: {}", e.getMessage(), e);
            // 실패 시 ETC로 fallback
            return new ClassifiedTopicResult(Topic.ETC, "잠시만 기다려주세요");
        }
    }


    private static String extractPureJson(String raw) {
        int start = raw.indexOf("{");
        int end = raw.lastIndexOf("}");
        if (start != -1 && end != -1 && start < end) {
            return raw.substring(start, end + 1);
        }
        log.error("JSON 블록을 찾을 수 없습니다.");
        throw new ChatBotException();
    }
}
