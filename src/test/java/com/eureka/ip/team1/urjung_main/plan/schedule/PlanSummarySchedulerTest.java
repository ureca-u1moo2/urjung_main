package com.eureka.ip.team1.urjung_main.plan.schedule;

import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import com.eureka.ip.team1.urjung_main.plan.scheduler.PlanSummaryScheduler;
import com.eureka.ip.team1.urjung_main.plan.service.PlanAiSummaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class PlanSummarySchedulerTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PlanAiSummaryService planAiSummaryService;

    @InjectMocks
    private PlanSummaryScheduler planSummaryScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateAllSummaries_AllSuccess() {
        Plan plan1 = Plan.builder().id("plan-1").build();
        Plan plan2 = Plan.builder().id("plan-2").build();
        when(planRepository.findAll()).thenReturn(List.of(plan1, plan2));

        planSummaryScheduler.updateAllSummaries();

        verify(planAiSummaryService).summarizePlanReview("plan-1");
        verify(planAiSummaryService).summarizePlanReview("plan-2");
    }

    @Test
    void testUpdateAllSummaries_WithException() {
        Plan plan1 = Plan.builder().id("plan-1").build();
        Plan plan2 = Plan.builder().id("plan-2").build();
        when(planRepository.findAll()).thenReturn(List.of(plan1, plan2));

        doThrow(new RuntimeException("요약 실패")).when(planAiSummaryService).summarizePlanReview("plan-1");

        planSummaryScheduler.updateAllSummaries();

        verify(planAiSummaryService).summarizePlanReview("plan-1");
        verify(planAiSummaryService).summarizePlanReview("plan-2"); // 두 번째는 정상
    }
}

