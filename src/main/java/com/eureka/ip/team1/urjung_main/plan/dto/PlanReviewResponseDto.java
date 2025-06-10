package com.eureka.ip.team1.urjung_main.plan.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 요금제 리뷰 Dto
@Getter
@Builder
public class PlanReviewResponseDto {

    private String id;
    private Long userId;
    private int rating;
    private String content;
    private LocalDateTime createdAt;
}
