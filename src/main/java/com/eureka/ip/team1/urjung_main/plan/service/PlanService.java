package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;

import java.util.List;

// 요금제 목록 Servire
public interface PlanService {

    // 요금제 목록 조회
    List<PlanDto> getAllPlans();

    // 요금제 상세 페이지
    PlanDetailDto getPlanDetail(String planId);

    // 요금제 2개 비교하기
    List<PlanDetailDto> comparePlans(List<String> planIds);

}
