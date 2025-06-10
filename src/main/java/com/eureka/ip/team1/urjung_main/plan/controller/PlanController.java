package com.eureka.ip.team1.urjung_main.plan.controller;

import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.Message;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    // 전체 요금제 목록 조회 API
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<PlanDto>>> getPlans() {
//        List<PlanDto> plans = planService.getAllPlans();

    // 요금제 조회 필터
    @GetMapping
    public ResponseEntity<?> getPlans(@RequestParam(defaultValue = "popular") String sortBy) {
        List<PlanDto> plans = planService.getPlansSorted(sortBy);



        return ResponseEntity.ok(
                ApiResponse.<List<PlanDto>>builder()
                        .message("SUCCESS")
                        .result(Result.SUCCESS)
                        .data(plans)
                        .build()
        );
    }

    // 특정 요금제 상세 조회 API
    @GetMapping("/{planId}")
    public ResponseEntity<ApiResponse<PlanDetailDto>> getPlanDetail(@PathVariable String planId) {
        PlanDetailDto planDetail = planService.getPlanDetail(planId);

        return ResponseEntity.ok(
                ApiResponse.<PlanDetailDto>builder()
                        .message("SUCCESS")
                        .result(Result.SUCCESS)
                        .data(planDetail)
                        .build()
        );
    }

    // 요금제 비교 API
    @GetMapping("/compare")
    public ResponseEntity<ApiResponse<List<PlanDetailDto>>> comparePlans(@RequestParam List<String> planIds) {
        List<PlanDetailDto> plans = planService.comparePlans(planIds);
        return ResponseEntity.ok(ApiResponse.<List<PlanDetailDto>>builder()
                .result(Result.SUCCESS)
                .data(plans)
                .message("SUCCESS")
                .build());
    }

}
