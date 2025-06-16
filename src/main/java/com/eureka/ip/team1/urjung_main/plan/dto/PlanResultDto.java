package com.eureka.ip.team1.urjung_main.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanResultDto {
    private String id;
    private String name;
    private String description;
    private int price;
    private long callAmount;
    private long dataAmount;
    private long smsAmount;
    private double score;
}
