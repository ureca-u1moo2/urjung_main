package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewRequestDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewResponseDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewUpdateDto;

import java.util.List;

// 요금제 리뷰 Service
public interface PlanReviewService {

    // 요금제 리뷰 목록
    List<PlanReviewResponseDto> getReviewsByPlanId(String planId);

    // 요금제 리뷰 등록
//    void createReview(String planId, Long userId, PlanReviewRequestDto requestDto);
    PlanReviewResponseDto createReview(String planId, Long userId, PlanReviewRequestDto requestDto);

    // 요금제 리뷰 수정
    void updateReview(String planId, String reviewId, Long userId, PlanReviewUpdateDto updateDto);

    // 요금제 리뷰 삭제
    void deleteReview(String planId, String reviewId, Long userId);

}
