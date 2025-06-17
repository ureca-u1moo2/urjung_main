package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.TopicBasedPrompts.PLANT_DETAIL_BASE_PROMPT;

@Component
public class PlanDetailPromptStrategy implements SingleArgsPromptStrategy {

    @Override
    public String generatePrompt(String plans) {
        return PLANT_DETAIL_BASE_PROMPT + "\n" + plans;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.PLAN_DETAIL);
    }


}
