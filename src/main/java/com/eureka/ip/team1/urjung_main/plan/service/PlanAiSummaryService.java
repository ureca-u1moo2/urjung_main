package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanAiSummaryResponseDto;

public interface PlanAiSummaryService {
    PlanAiSummaryResponseDto summarizePlanReview(String planId);
}
