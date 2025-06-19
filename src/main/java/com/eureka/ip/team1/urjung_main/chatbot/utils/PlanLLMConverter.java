package com.eureka.ip.team1.urjung_main.chatbot.utils;

import com.eureka.ip.team1.urjung_main.chatbot.dto.PlanForLLMDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.entity.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class PlanLLMConverter {

    public static List<PlanForLLMDto> convertToLLMDto(List<PlanDto> plans) {
        return plans.stream()
                .map(plan -> {
                    String dataText;
                    if (plan.getDataAmount() != null) {
                        if (plan.getDataAmount() < 0) {
                            dataText = "무제한";
                        } else if (plan.getDataAmount() < 1024) {
                            dataText = plan.getDataAmount() + "MB";
                        } else {
                            dataText = (plan.getDataAmount() / 1024) + "GB";
                        }
                    } else {
                        dataText = "제공 없음";
                    }

                    return PlanForLLMDto.builder()
                            .id(plan.getId())
                            .name(plan.getName())
                            .price(plan.getPrice())
                            .description(plan.getDescription())
                            .dataAmountText(dataText)
                            .callAmount(plan.getCallAmount())
                            .smsAmount(plan.getSmsAmount())
                            .tags(plan.getTags().stream().map(Tag::getTagName).collect(Collectors.toList()))
                            .build();
                })
                .toList();
    }
}
