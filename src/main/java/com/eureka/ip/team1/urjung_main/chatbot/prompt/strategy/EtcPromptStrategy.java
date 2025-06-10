package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EtcPromptStrategy implements NoArgsPromptStrategy{
    private static final String ETC_BASE_PROMPT =
            """
                  우리 서비스와 관계없는 질문입니다. 간단하지만 재치있게 우리 서비스로의 유도로 응대하세요.
                    """;
    @Override
    public String generatePrompt() {
        return ETC_BASE_PROMPT;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.ETC);
    }


}
