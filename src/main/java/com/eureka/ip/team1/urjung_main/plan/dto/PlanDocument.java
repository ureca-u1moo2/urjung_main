package com.eureka.ip.team1.urjung_main.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanDocument {
    private String id;
    private String name;
    private String description;
    private int price;
    private Long call_amount;
    private Long data_amount;
    private Long sms_amount;
    private List<Float> embedding;
}

