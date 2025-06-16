package com.eureka.ip.team1.urjung_main.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LineDto {
    private String id;
    private String userId;
    private String phoneNumber;
    private String planId;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int discountedPrice;
}
