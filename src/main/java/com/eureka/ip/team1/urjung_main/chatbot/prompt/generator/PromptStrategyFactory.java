package com.eureka.ip.team1.urjung_main.chatbot.prompt.generator;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.PromptStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PromptStrategyFactory {

    private final List<PromptStrategy> strategies;
    private final Map<Topic, PromptStrategy> strategyMap = new EnumMap<>(Topic.class);

    @PostConstruct
    public void init() {
        for (PromptStrategy strategy : strategies) {
            strategy.support().ifPresent(topic -> strategyMap.put(topic, strategy));
        }
    }

    public PromptStrategy getStrategy(Topic topic) {
        PromptStrategy strategy = strategyMap.get(topic);
        if (strategy == null) {
            throw new IllegalArgumentException("해당 토픽을 지원하는 프롬프트 전략이 없습니다: " + topic);
        }
        return strategy;
    }
}
