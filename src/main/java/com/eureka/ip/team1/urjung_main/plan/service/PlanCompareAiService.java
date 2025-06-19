package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanCompareAiResponseDto;

import java.util.List;

public interface PlanCompareAiService {
    PlanCompareAiResponseDto analyzeComparison(List<String> planIds);
}
