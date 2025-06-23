package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.TopicPrompts.RECOMMENDATION_BASE_PROMPT;

@Component
public class RecommendPromptStrategy implements SingleArgsPromptStrategy {

    @Override
    public String generatePrompt(String data) {
        return RECOMMENDATION_BASE_PROMPT + "\n" + data;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.RECOMMENDATION_PLAN);
    }


}
