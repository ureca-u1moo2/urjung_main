package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.TopicBasedPrompts.ALL_PLAN;
import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.TopicBasedPrompts.ETC_BASE_PROMPT;

@Component
public class AllPlanPromptStrategy implements NoArgsPromptStrategy {

    @Override
    public String generatePrompt() {
        return ALL_PLAN ;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.ALL_PLAN_LIST);
    }


}
