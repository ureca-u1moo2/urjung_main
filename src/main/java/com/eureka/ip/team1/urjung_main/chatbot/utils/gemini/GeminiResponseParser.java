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
            if( root.has("need_my_plan")){
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
        String[] split = raw.split(":", 2);
        String topicStr = split[0].trim();
        String waitMessage = (split.length > 1) ? split[1].trim() : "";

        try {
            Topic topic = Topic.valueOf(topicStr);
            return new ClassifiedTopicResult(topic, waitMessage);
        } catch (IllegalArgumentException e) {
            log.error("토픽 변환 실패: {}", e.getMessage());
            throw new ChatBotException();
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
