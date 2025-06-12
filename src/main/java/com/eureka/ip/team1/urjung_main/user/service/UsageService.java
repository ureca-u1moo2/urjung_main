package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.user.dto.UsageRequestDto;
import com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto;

import java.util.List;
import java.util.Optional;

public interface UsageService {

    // 특정 유저의 모든 월별 사용량 조회
    List<UsageResponseDto> getAllUsagesByUserId(UsageRequestDto usageRequestDto);

    // 특정 유저의 특정 월 사용량 조회
    List<UsageResponseDto> getAllUsagesByUserIdAndMonth(UsageRequestDto usageRequestDto);

    // 금월 사용량 조회
    List<UsageResponseDto> getCurrentMonthUsagesByUserId(UsageRequestDto usageRequestDto);

    // 특정 유저의 특정 요금제 특정 월 사용량 조회
    Optional<UsageResponseDto> getUsageByLineIdAndMonth(UsageRequestDto usageRequestDto);

    Optional<UsageResponseDto> getUsageByUserIdAndPlanIdAndMonth(UsageRequestDto usageRequestDto);

    Optional<UsageResponseDto> getCurrentMonthUsageByUserIdAndPhoneNumber(UsageRequestDto usageRequestDto);

}
