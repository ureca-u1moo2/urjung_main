package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// 요금제 목록 ServiceImpl

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    // 요금제 전체 목록 조회
    @Override
    public List<PlanDto> getAllPlans() {
        List<Plan> plans = planRepository.findAll();

        return plans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PlanDto convertToDto(Plan plan) {
        return PlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .price(plan.getPrice())
                .description(plan.getDescription())
                .dataAmount(plan.getDataAmount())
                .callAmount(plan.getCallAmount())
                .smsAmount(plan.getSmsAmount())
                .createdAt(plan.getCreatedAt())
                .build();
    }

    // 요금제 상세 페이지 조회
    @Override
    public PlanDetailDto getPlanDetail(String planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("요금제를 찾을 수 없습니다."));

        return convertToDetailDto(plan);
    }
    private PlanDetailDto convertToDetailDto(Plan plan) {
        return PlanDetailDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .price(plan.getPrice())
                .description(plan.getDescription())
                .dataAmount(plan.getDataAmount())
                .callAmount(plan.getCallAmount())
                .smsAmount(plan.getSmsAmount())
                .createdAt(plan.getCreatedAt())
                .build();
    }

    // 요금제 비교 페이지
    @Override
    public List<PlanDetailDto> comparePlans(List<String> planIds) {
        List<Plan> plans = planRepository.findAllById(planIds);

        // 예외 처리 (선택된 요금제가 부족할 경우)
        if (plans.size() != planIds.size()) {
            throw new NotFoundException("일부 요금제를 찾을 수 없습니다.");
        }

        // 변환 후 리턴
        return plans.stream()
                .map(plan -> PlanDetailDto.builder()
                        .id(plan.getId())
                        .name(plan.getName())
                        .price(plan.getPrice())
                        .description(plan.getDescription())
                        .dataAmount(plan.getDataAmount())
                        .callAmount(plan.getCallAmount())
                        .smsAmount(plan.getSmsAmount())
                        .createdAt(plan.getCreatedAt())
                        .build())
                .toList();
    }
}
