package com.eureka.ip.team1.urjung_main.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@ToString
public class UsageResponseDto {

    private String planId;

    private String phoneNumber;

    private Integer year;

    private Integer month;

    private Long data;

    private Long callMinute;

    private Long message;

}
