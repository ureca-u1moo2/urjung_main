package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.eureka.ip.team1.urjung_main.chatbot.constants.AIPromptMessages.SYSTEM_INFO_BASE_PROMPT;

@Component
public class ServiceInfoPromptStrategy implements NoArgsPromptStrategy {
    @Override
    public String generatePrompt() {
        return SYSTEM_INFO_BASE_PROMPT;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.INFO);
    }
}
