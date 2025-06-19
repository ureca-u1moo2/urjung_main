package com.eureka.ip.team1.urjung_main.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlanCompareAiResponseDto {
    private List<PlanDetailDto> plans;
    private String aiSummary; // AI 요약 문장
}

