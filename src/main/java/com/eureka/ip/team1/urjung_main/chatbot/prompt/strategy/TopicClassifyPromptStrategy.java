package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TopicClassifyPromptStrategy implements NoArgsPromptStrategy {
    private static final String TOPIC_CLASSIFY_BASE_PROMPT =
            "다음 사용자의 메시지를 보고 적절한 주제를 아래 항목 중 하나로만 선택한 후, 콜론(:) 뒤에 정해진 토픽과 사용자 메세지를 바탕으로 간단한 안내 문구를 함께 작성해 주세요.\n" +
                    "형식은 반드시 `Topic명: 안내 메시지` 형식으로 해주세요. 안내 메세지 끝에는 항상 잠시만 기다려주세요를 붙혀주세요. 만약 TOPIC이 ETC일 경우 '잠시만 기다려주세요'만 내보내 주세요.\n" +
                    "예: `RECOMMENDATION_PLAN: 고객님께 어울리는 요금제를 추천해드릴게요.`\n\n" +
                    "=== Topic 목록 ===\n%s\n\n" +
                    "=== 사용자 메시지 ===\n";

    private static final String topicList = Arrays.stream(Topic.values())
            .map(t -> t.name() + ": " + t.getDescription())
            .collect(Collectors.joining("\n"));

    @Override
    public String generatePrompt() {
        return String.format(TOPIC_CLASSIFY_BASE_PROMPT, topicList);
    }

    @Override
    public Optional<Topic> support() {
        return Optional.empty(); // TopicClassifyPromptStrategy
    }
}
