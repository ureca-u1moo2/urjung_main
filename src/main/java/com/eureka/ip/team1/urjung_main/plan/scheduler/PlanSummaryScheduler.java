package com.eureka.ip.team1.urjung_main.plan.scheduler;

import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import com.eureka.ip.team1.urjung_main.plan.service.PlanAiSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlanSummaryScheduler {

    private final PlanRepository planRepository;
    private final PlanAiSummaryService planAiSummaryService;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    public void updateAllSummaries() {
        List<Plan> plans = planRepository.findAll();
        for (Plan plan : plans) {
            try {
                planAiSummaryService.summarizePlanReview(plan.getId());
            } catch (Exception e) {
                log.error("[리뷰 요약 실패] planId: {}", plan.getId(), e);
            }
        }
    }
}
