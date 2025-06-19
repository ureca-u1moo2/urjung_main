//package com.eureka.ip.team1.urjung_main.plan.service;
//
//import com.eureka.ip.team1.urjung_main.plan.dto.PlanAiSummaryResponseDto;
//import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
//import com.eureka.ip.team1.urjung_main.plan.entity.PlanReview;
//import com.eureka.ip.team1.urjung_main.plan.entity.PlanSummary;
//import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
//import com.eureka.ip.team1.urjung_main.plan.repository.PlanReviewRepository;
//import com.eureka.ip.team1.urjung_main.plan.repository.PlanSummaryRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class PlanAiSummaryServiceImplTest {
//
//    @Mock
//    private PlanReviewRepository planReviewRepository;
//
//    @Mock
//    private PlanRepository planRepository;
//
//    @Mock
//    private PlanSummaryRepository planSummaryRepository;
//
//    @Mock
//    private GeminiApiClient geminiApiClient;
//
//    @Mock
//    private RedisTemplate<String, String> redisTemplate;
//
//    @Mock
//    private ValueOperations<String, String> valueOperations;
//
//    @InjectMocks
//    private PlanAiSummaryServiceImpl planAiSummaryService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSummarizePlanReview_ReturnsExistingSummaryWithin24Hours() {
//        Plan plan = Plan.builder().id("plan-1").build();
//        PlanSummary summary = PlanSummary.builder()
//                .id("summary-1")
//                .plan(plan)
//                .summaryText("요약 내용")
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
//        when(planSummaryRepository.findByPlan(plan)).thenReturn(Optional.of(summary));
//
//        PlanAiSummaryResponseDto result = planAiSummaryService.summarizePlanReview("plan-1");
//
//        assertEquals("요약 내용", result.getSummary());
//    }
//
//    @Test
//    void testSummarizePlanReview_CreatesNewSummaryWhenExpired() {
//        Plan plan = Plan.builder().id("plan-2").build();
//        List<PlanReview> reviews = List.of(
//                PlanReview.builder().content("좋아요").build(),
//                PlanReview.builder().content("별로예요").build()
//        );
//
//        PlanSummary oldSummary = PlanSummary.builder()
//                .id("old-id")
//                .plan(plan)
//                .summaryText("오래된 요약")
//                .updatedAt(LocalDateTime.now().minusDays(2))
//                .build();
//
//        when(planRepository.findById("plan-2")).thenReturn(Optional.of(plan));
//        when(planSummaryRepository.findByPlan(plan)).thenReturn(Optional.of(oldSummary));
//        when(planReviewRepository.findByPlanId("plan-2")).thenReturn(reviews);
//        when(geminiApiClient.getGeminiSummary(any(), any())).thenReturn("새로운 요약");
//
//        PlanAiSummaryResponseDto result = planAiSummaryService.summarizePlanReview("plan-2");
//
//        assertEquals("새로운 요약", result.getSummary());
//        verify(planSummaryRepository).save(any());
//    }
//
//    @Test
//    void testSummarizePlanReview_CreatesNewSummaryWhenNoPreviousExists() {
//        Plan plan = Plan.builder().id("plan-3").build();
//        List<PlanReview> reviews = List.of(
//                PlanReview.builder().content("아주 좋아요").build()
//        );
//
//        when(planRepository.findById("plan-3")).thenReturn(Optional.of(plan));
//        when(planSummaryRepository.findByPlan(plan)).thenReturn(Optional.empty());
//        when(planReviewRepository.findByPlanId("plan-3")).thenReturn(reviews);
//        when(geminiApiClient.getGeminiSummary(any(), any())).thenReturn("요약 생성 완료");
//
//        PlanAiSummaryResponseDto result = planAiSummaryService.summarizePlanReview("plan-3");
//
//        assertEquals("요약 생성 완료", result.getSummary());
//        verify(planSummaryRepository).save(any());
//    }
//
//    @Test
//    void testSummarizePlanReview_ReturnsFallbackMessageWhenNoReviews() {
//        Plan plan = Plan.builder().id("plan-4").build();
//
//        when(planRepository.findById("plan-4")).thenReturn(Optional.of(plan));
//        when(planSummaryRepository.findByPlan(plan)).thenReturn(Optional.empty());
//        when(planReviewRepository.findByPlanId("plan-4")).thenReturn(Collections.emptyList());
//
//        PlanAiSummaryResponseDto result = planAiSummaryService.summarizePlanReview("plan-4");
//
//        assertEquals("리뷰가 없어 요약할 수 없습니다.", result.getSummary());
//        verify(planSummaryRepository, never()).save(any());
//        verify(geminiApiClient, never()).getGeminiSummary(any(), any());
//    }
//
//    @Test
//    void testSummarizePlanReview_ThrowsWhenPlanNotFound() {
//        when(planRepository.findById("plan-404")).thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            planAiSummaryService.summarizePlanReview("plan-404");
//        });
//    }
//
//    @Test
//    void testSummarizePlanReview_ReturnsFromRedisCache() {
//        String planId = "plan-redis";
//        String cached = "캐시된 요약";
//
//        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//        when(valueOperations.get("plan:summary:" + planId)).thenReturn(cached);
//
//        PlanAiSummaryServiceImpl service = new PlanAiSummaryServiceImpl(
//                planReviewRepository,
//                planRepository,
//                planSummaryRepository,
//                geminiApiClient,
//                redisTemplate
//        );
//
//        PlanAiSummaryResponseDto result = service.summarizePlanReview(planId);
//        assertEquals(cached, result.getSummary());
//
//        verify(redisTemplate.opsForValue(), times(1)).get("plan:summary:" + planId);
//        verify(planRepository, never()).findById(any());
//    }
//}
package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanAiSummaryResponseDto;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.entity.PlanReview;
import com.eureka.ip.team1.urjung_main.plan.entity.PlanSummary;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanReviewRepository;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanSummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlanAiSummaryServiceTest {

    @Mock private PlanReviewRepository planReviewRepository;
    @Mock private PlanRepository planRepository;
    @Mock private PlanSummaryRepository planSummaryRepository;
    @Mock private GeminiApiClient geminiApiClient;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private ValueOperations<String, String> valueOps;

    @InjectMocks private PlanAiSummaryServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    void testRedisHit() {
        String planId = "abc";
        when(valueOps.get("plan:summary:" + planId)).thenReturn("캐시된 요약");

        PlanAiSummaryResponseDto result = service.summarizePlanReview(planId);

        assertEquals("캐시된 요약", result.getSummary());
        verify(planRepository, never()).findById(any());
    }

    @Test
    void testNoReviews() {
        String planId = "def";
        Plan plan = Plan.builder().id(planId).build();

        when(valueOps.get("plan:summary:" + planId)).thenReturn(null);
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(planReviewRepository.findByPlanId(planId)).thenReturn(Collections.emptyList());

        PlanAiSummaryResponseDto result = service.summarizePlanReview(planId);

        assertEquals("리뷰가 없어 요약할 수 없습니다.", result.getSummary());
        verify(planSummaryRepository, never()).save(any());
    }

    @Test
    void testNewSummaryWithExistingSummaryObject() {
        String planId = "ghi";
        Plan plan = Plan.builder().id(planId).build();
        List<PlanReview> reviews = List.of(PlanReview.builder().content("좋아요").build());
        PlanSummary summary = PlanSummary.builder().plan(plan).updatedAt(LocalDateTime.now().minusDays(1)).build();

        when(valueOps.get("plan:summary:" + planId)).thenReturn(null);
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(planReviewRepository.findByPlanId(planId)).thenReturn(reviews);
        when(geminiApiClient.getGeminiSummary(any(), any())).thenReturn("요약 결과");
        when(planSummaryRepository.findByPlan(plan)).thenReturn(Optional.of(summary));

        PlanAiSummaryResponseDto result = service.summarizePlanReview(planId);

        assertEquals("요약 결과", result.getSummary());
        verify(planSummaryRepository).save(any());
        verify(valueOps).set("plan:summary:" + planId, "요약 결과", Duration.ofHours(24));
    }

    @Test
    void testNewSummaryWithNoSummaryObject() {
        String planId = "jkl";
        Plan plan = Plan.builder().id(planId).build();
        List<PlanReview> reviews = List.of(PlanReview.builder().content("굳").build());

        when(valueOps.get("plan:summary:" + planId)).thenReturn(null);
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(planReviewRepository.findByPlanId(planId)).thenReturn(reviews);
        when(geminiApiClient.getGeminiSummary(any(), any())).thenReturn("새로운 요약");
        when(planSummaryRepository.findByPlan(plan)).thenReturn(Optional.empty());

        PlanAiSummaryResponseDto result = service.summarizePlanReview(planId);

        assertEquals("새로운 요약", result.getSummary());
        verify(planSummaryRepository).save(any());
        verify(valueOps).set("plan:summary:" + planId, "새로운 요약", Duration.ofHours(24));
    }

    @Test
    void testPlanNotFound() {
        String planId = "404";
        when(valueOps.get("plan:summary:" + planId)).thenReturn(null);
        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.summarizePlanReview(planId);
        });
    }
}
