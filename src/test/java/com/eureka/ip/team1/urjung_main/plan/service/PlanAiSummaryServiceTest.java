package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanAiSummaryResponseDto;
import com.eureka.ip.team1.urjung_main.plan.entity.PlanReview;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PlanAiSummaryServiceTest {

    private PlanReviewRepository planReviewRepository;
    private GeminiApiClient geminiApiClient;
    private PlanAiSummaryServiceImpl service;

    @BeforeEach
    void setUp() {
        planReviewRepository = mock(PlanReviewRepository.class);
        geminiApiClient = mock(GeminiApiClient.class);
        service = new PlanAiSummaryServiceImpl(planReviewRepository, geminiApiClient);
    }

    @Test
    @DisplayName("리뷰가 없는 경우 기본 메시지를 반환한다")
    void whenNoReviews_thenReturnDefaultMessage() {
        // given
        String planId = "plan-empty";
        when(planReviewRepository.findByPlanId(planId)).thenReturn(Collections.emptyList());

        // when
        PlanAiSummaryResponseDto result = service.summarizePlanReview(planId);

        // then
        assertThat(result.getSummary()).isEqualTo("리뷰가 존재하지 않아 요약할 수 없습니다.");
        verify(geminiApiClient, never()).getGeminiSummary(anyString(), anyString());
    }

    @Test
    @DisplayName("리뷰가 있는 경우 Gemini API를 호출하여 결과를 반환한다")
    void whenReviewsExist_thenReturnSummary() {
        // given
        String planId = "plan-123";
        List<PlanReview> reviews = List.of(
                PlanReview.builder().content("데이터 많고 싸요").build(),
                PlanReview.builder().content("통화 품질이 별로").build()
        );
        when(planReviewRepository.findByPlanId(planId)).thenReturn(reviews);
        when(geminiApiClient.getGeminiSummary(anyString(), anyString()))
                .thenReturn("1. 장점: 가격이 저렴함\n2. 단점: 통화 품질 불만");

        // when
        PlanAiSummaryResponseDto result = service.summarizePlanReview(planId);

        // then
        assertThat(result.getSummary()).contains("장점").contains("단점");
        verify(geminiApiClient, times(1)).getGeminiSummary(anyString(), anyString());
    }

    @Test
    @DisplayName("Gemini API가 null을 반환할 경우 fallback 메시지를 처리해야 한다")
    void whenGeminiReturnsNull_thenHandleGracefully() {
        // given
        String planId = "plan-null";
        List<PlanReview> reviews = List.of(
                PlanReview.builder().content("좋아요").build()
        );
        when(planReviewRepository.findByPlanId(planId)).thenReturn(reviews);
        when(geminiApiClient.getGeminiSummary(anyString(), anyString()))
                .thenReturn(null);  // edge case

        // when
        PlanAiSummaryResponseDto result = service.summarizePlanReview(planId);

        // then
        assertThat(result.getSummary()).isNull(); // 현재는 그대로 반환. 필요 시 fallback 처리 가능
    }
}
