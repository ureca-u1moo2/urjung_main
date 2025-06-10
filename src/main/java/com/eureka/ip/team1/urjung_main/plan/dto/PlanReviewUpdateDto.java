package com.eureka.ip.team1.urjung_main.plan.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanReviewUpdateDto {
    private int rating;
    private String content;
}
