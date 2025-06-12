package com.eureka.ip.team1.urjung_main.plan.dto;

import lombok.*;

import java.time.LocalDateTime;

// 요금제 상세 페이지 Dto
// 요금제 비교 Dto
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDetailDto {
    private String id;
    private String name;
    private int price;
    private String description;
    private Long dataAmount;
    private Long callAmount;
    private Long smsAmount;
    private LocalDateTime createdAt;

    // 요금제 무제한인 경우
    public String getDataAmountText() {
        if (dataAmount != null && dataAmount < 0) return "무제한";
        return dataAmount + "GB";
    }
}
