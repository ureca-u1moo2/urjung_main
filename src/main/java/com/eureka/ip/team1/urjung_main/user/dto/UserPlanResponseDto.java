package com.eureka.ip.team1.urjung_main.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
public class UserPlanResponseDto {
    // 요금제 ID
    private String planId;

    // 요금제 이름
    private String planName;

    // 휴대폰 번호
    private String phoneNumber;

    // 요금제 설명
    private String description;

    // 요금제 가입 시작일
    private LocalDateTime startDate;

    // 가입 가격 (할인 적용된 가격)
    private Integer discountedPrice;

}
