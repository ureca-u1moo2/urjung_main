package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.eureka.ip.team1.urjung_main.chatbot.constants.AIPromptMessages.PLAN_LIST;

@Component
public class FilteredPlanPromptStrategy implements SingleArgsPromptStrategy {
    @Override
    public String generatePrompt(String plans) {
        return PLAN_LIST + "\n" + plans;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.PLAN_LIST);
    }


}
