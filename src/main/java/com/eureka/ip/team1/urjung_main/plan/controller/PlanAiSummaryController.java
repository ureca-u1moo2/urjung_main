//package com.eureka.ip.team1.urjung_main.plan.controller;
//
//import com.eureka.ip.team1.urjung_main.common.ApiResponse;
//import com.eureka.ip.team1.urjung_main.common.enums.Result;
//import com.eureka.ip.team1.urjung_main.plan.dto.PlanAiSummaryResponseDto;
//import com.eureka.ip.team1.urjung_main.plan.service.PlanAiSummaryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/plans")
//@RequiredArgsConstructor
//public class PlanAiSummaryController {
//
//    private final PlanAiSummaryService planAiSummaryService;
//
//    @GetMapping("/{planId}/reviews/summary")
////    public ResponseEntity<PlanAiSummaryResponseDto> getReviewSummary(@PathVariable String planId) {
////        return ResponseEntity.ok(planAiSummaryService.summarizePlanReview(planId));
////    }
//    public ResponseEntity<ApiResponse<PlanAiSummaryResponseDto>> getReviewSummary(@PathVariable String planId) {
//        PlanAiSummaryResponseDto summaryDto = planAiSummaryService.summarizePlanReview(planId);
//
//        return ResponseEntity.ok(
//                ApiResponse.<PlanAiSummaryResponseDto>builder()
//                        .message("SUCCESS")
//                        .result(Result.SUCCESS)
//                        .data(summaryDto)
//                        .build()
//        );
//    }
//}
package com.eureka.ip.team1.urjung_main.plan.controller;

import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanAiSummaryResponseDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanAiSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanAiSummaryController {

    private final PlanAiSummaryService planAiSummaryService;

    // 요금제 리뷰 요약 조회
    @GetMapping("/{planId}/reviews/summary")
    public ResponseEntity<ApiResponse<PlanAiSummaryResponseDto>> getReviewSummary(@PathVariable String planId) {
        PlanAiSummaryResponseDto summaryDto = planAiSummaryService.summarizePlanReview(planId);

        return ResponseEntity.ok(
                ApiResponse.<PlanAiSummaryResponseDto>builder()
                        .message("SUCCESS")
                        .result(Result.SUCCESS)
                        .data(summaryDto)
                        .build()
        );
    }
}