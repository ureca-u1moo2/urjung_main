package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.TopicPrompts.ETC_BASE_PROMPT;

@Component
public class EtcPromptStrategy implements NoArgsPromptStrategy {

    @Override
    public String generatePrompt() {
        return ETC_BASE_PROMPT;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.ETC);
    }


}
