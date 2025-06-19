package com.eureka.ip.team1.urjung_main.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PlanForLLMDto {
    private String id;
    private String name;
    private int price;
    private String description;
    private String dataAmountText; // 👈 단위 처리된 텍스트
    private long callAmount;
    private long smsAmount;
    private List<String> tags;
}