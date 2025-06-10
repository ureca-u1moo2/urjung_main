package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AllPlanPromptStrategy implements NoArgsPromptStrategy{
    private static final String ALL_PLAN_BASE_PROMPT =
            """
                  전체 요금제는 아래의 버튼을 눌러 확인할 수 있다고 알려주세요. 필요하다면 고객님에게 필요한 요금제를 추천해줄 수 도 있습니. 
                  텍스트 중간에 [전체요금제] 이런 형식은 쓰지말아주세요.
                    """;
    @Override
    public String generatePrompt() {
        return ALL_PLAN_BASE_PROMPT;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.ALL_PLAN_INFORMATION);
    }


}
