package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FilteredPlanPromptStrategy implements SingleArgsPromptStrategy{
    private static final String FILTERED_PLAN_BASE_PROMPT =
            """
                  아래의 요금제 목록에서 사용자가 특정하거나, 사용자가 말하는 사용자의 성향과 어울리거나, 사용자가 요구하는 사항에 가장 만족하는 요금제를 최대 3개까지 뽑아 추천해주세요. 
                  만약 없다면 유하게 받아쳐주세요. 그리고 해당 요금제 리스트는 인기가 많은 순부터 낮은 순으로 되어 있습니다.
                    """;
    @Override
    public String generatePrompt(String plans) {
        return FILTERED_PLAN_BASE_PROMPT+"\n"+plans;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.FILTERED_PLAN_LIST);
    }


}
