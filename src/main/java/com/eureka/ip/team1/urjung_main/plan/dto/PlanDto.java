package com.eureka.ip.team1.urjung_main.plan.dto;

import lombok.*;

import java.time.LocalDateTime;

// 요금제 전체 목록 Dto
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDto {

    private String id;
    private String name;
    private int price;
    private String description;
    private Long dataAmount;
    private Long callAmount;
    private Long smsAmount;
    private LocalDateTime createdAt;
}
