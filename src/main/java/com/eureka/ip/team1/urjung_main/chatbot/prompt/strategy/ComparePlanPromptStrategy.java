package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ComparePlanPromptStrategy implements SingleArgsPromptStrategy{
    private static final String COMPARE_PLAN_BASE_PROMPT =
            """
                  아래의 요금제 목록에서 사용자가 원하는 플랜들을 찾아 차이점 중심으로 답하세요. 만약 사용자가 입력한 요금제와 일치하는 요금제가 없으면 
                  다시한번 질문해주세요
                    """;
    @Override
    public String generatePrompt(String plans) {
        return COMPARE_PLAN_BASE_PROMPT+"\n"+plans;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.COMPARE_PLAN_WITHOUT_MY_PLAN);
    }


}
