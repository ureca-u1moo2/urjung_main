package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.TopicPrompts.COMPARE_PLAN_BASE_PROMPT;

@Component
public class ComparePlanPromptStrategy implements SingleArgsPromptStrategy {
    @Override
    public String generatePrompt(String plans) {
        return COMPARE_PLAN_BASE_PROMPT + "\n" + plans;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.COMPARE_PLAN);
    }


}
