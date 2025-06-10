package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PlanDetailPromptStrategy implements SingleArgsPromptStrategy{
    private static final String PLANT_DETAIL_BASE_PROMPT =
            """
                  아래의 요금제 목록에서 사용자가 원하는 플랜을 찾아 답하세요. description부분을 읽고 최대한 친절히 답하세요
                    """;
    @Override
    public String generatePrompt(String plans) {
        return PLANT_DETAIL_BASE_PROMPT+"\n"+plans;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.PLAN_DETAIL);
    }


}
