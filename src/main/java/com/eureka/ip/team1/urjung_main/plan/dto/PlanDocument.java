package com.eureka.ip.team1.urjung_main.plan.dto;

import co.elastic.clients.elasticsearch._types.mapping.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;
import java.util.Map;

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
    private Map<String, Float> rankScores;
}

