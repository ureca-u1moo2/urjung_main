package com.eureka.ip.team1.urjung_main.chatbot.utils;

import com.eureka.ip.team1.urjung_main.chatbot.component.Card;
import com.eureka.ip.team1.urjung_main.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CardFactory {
    private final PlanProvider planProvider;

    public List<Card> createFromPlanIds(List<String> planIds) {
        return planIds.stream()
                .map(planProvider::getPlanById)
                .map(plan -> Card.builder()
                        .value(plan)
                        .build())
                .toList();
    }
}