package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.eureka.ip.team1.urjung_main.chatbot.constants.AIPromptMessages.FILTERED_PLAN_BASE_PROMPT;

@Component
public class FilteredPlanPromptStrategy implements SingleArgsPromptStrategy {
    @Override
    public String generatePrompt(String plans) {
        return FILTERED_PLAN_BASE_PROMPT + "\n" + plans;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.FILTERED_PLAN_LIST);
    }


}
