package com.eureka.ip.team1.urjung_main.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageRequestDto {

    private String userId;

    private String lineId;

    private String planId;

    private Integer year;

    private Integer month;

}