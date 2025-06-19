package com.eureka.ip.team1.urjung_main.plan.controller;

import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanCompareAiResponseDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanCompareAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanCompareAiController {

    private final PlanCompareAiService planCompareAiService;
    // 요금제 비교 분석 AI
    @GetMapping("/compare/ai")
    public ResponseEntity<ApiResponse<PlanCompareAiResponseDto>> analyzeComparison(
            @RequestParam List<String> planIds) {
        PlanCompareAiResponseDto result = planCompareAiService.analyzeComparison(planIds);

        return ResponseEntity.ok(
                ApiResponse.<PlanCompareAiResponseDto>builder()
                        .message("SUCCESS")
                        .result(Result.SUCCESS)
                        .data(result)
                        .build()
        );
    }
}
