package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import org.springframework.data.domain.Page;

import java.util.List;

// 요금제 목록 Servire
//public interface PlanService {
//
//    // 요금제 목록 조회
////    List<PlanDto> getAllPlans();
//
//    // 요금제 목록 필터링
////    List<PlanDto> getPlansSorted(String sortBy);
//
//    // 요금제 상세 페이지
//    PlanDetailDto getPlanDetail(String planId);
//
//    // 요금제 2개 비교하기
//    List<PlanDetailDto> comparePlans(List<String> planIds);
//
//    // 요금제 목록 페이징
//    Page<PlanDto> getPlansSorted(String sortBy, int page, int size);
//
//}
public interface PlanService {
    List<PlanDto> getPlansSorted(String sortBy); // 기존 챗봇 연동 유지
    Page<PlanDto> getPlansSorted(String sortBy, int page, int size); // 페이징용
    PlanDetailDto getPlanDetail(String planId);
    List<PlanDetailDto> comparePlans(List<String> planIds);
}
