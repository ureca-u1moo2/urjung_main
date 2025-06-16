package com.eureka.ip.team1.urjung_main.plan.dto;

import com.eureka.ip.team1.urjung_main.plan.entity.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<Tag> tags;

    // 데이터 무제한 인 경우
    public String getDataAmountText() {
        if (dataAmount != null && dataAmount < 0) return "무제한";
        return dataAmount + "GB";
    }
}
