package com.eureka.ip.team1.urjung_main.plan.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewRequestDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewResponseDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewUpdateDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 요금제 리뷰
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanReviewController {

    private final PlanReviewService planReviewService;

    // 요금제 리뷰 목록 조회
    @GetMapping("/{planId}/reviews")
    public ResponseEntity<ApiResponse<List<PlanReviewResponseDto>>> getReviewsByPlanId(@PathVariable String planId) {
        List<PlanReviewResponseDto> reviews = planReviewService.getReviewsByPlanId(planId);
        return ResponseEntity.ok(ApiResponse.<List<PlanReviewResponseDto>>builder()
                .result(Result.SUCCESS)
                .data(reviews)
                .message("SUCCESS")
                .build());
    }

    // 요금제 리뷰 등록
    @PostMapping("/{planId}/reviews")
    public ResponseEntity<ApiResponse<Void>> createReview(
            @PathVariable String planId,
            @RequestBody PlanReviewRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 임시 userId → 추후 로그인 인증 정보에서 가져오는 걸로 수정 예정
//        Long userId = 1L;  // 예: 현재 테스트용으로 임시로 userId=1 사용
        String userId = userDetails.getUserId();

        planReviewService.createReview(planId, userId, requestDto);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .result(Result.SUCCESS)
                .message("SUCCESS")
                .build());
    }

    // 요금제 리뷰 수정
    @PatchMapping("/{planId}/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> updateReview(
            @PathVariable String planId,
            @PathVariable String reviewId,
            @RequestBody PlanReviewUpdateDto updateDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        Long userId = 1L;  // 임시
        String userId = userDetails.getUserId();

        planReviewService.updateReview(planId, reviewId, userId, updateDto);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .result(Result.SUCCESS)
                .message("SUCCESS")
                .build());
    }

    // 요금제 리뷰 삭제
    @DeleteMapping("/{planId}/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable String planId,
            @PathVariable String reviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        Long userId = 1L;  // 임시 userId
        String userId = userDetails.getUserId();

        planReviewService.deleteReview(planId, reviewId, userId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .result(Result.SUCCESS)
                .message("SUCCESS")
                .build());
    }


}
