package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.TopicPrompts.COMPARE_WITH_MY_PLAN_BASE_PROMPT;

@Component
public class CompareWithMyPlanPromptStrategy implements NoArgsPromptStrategy {
    @Override
    public String generatePrompt() {
        return COMPARE_WITH_MY_PLAN_BASE_PROMPT;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.COMPARE_WITH_MY_PLAN);
    }


}
