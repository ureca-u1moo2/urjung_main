package com.eureka.ip.team1.urjung_main.chatbot.component;


import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Card {
    private PlanDetailDto value;
}
