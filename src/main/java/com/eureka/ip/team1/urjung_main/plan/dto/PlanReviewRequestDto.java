package com.eureka.ip.team1.urjung_main.plan.dto;

import lombok.*;

// 요금제 리뷰 등록 Dto
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanReviewRequestDto {
    private int rating;
    private String content;
}
