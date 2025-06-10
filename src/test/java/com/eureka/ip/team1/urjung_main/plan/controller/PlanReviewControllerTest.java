package com.eureka.ip.team1.urjung_main.plan.controller;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewRequestDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewResponseDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;


@WebMvcTest(PlanReviewController.class)
public class PlanReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanReviewService planReviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("리뷰 목록 조회 성공")
    void getReviews_success() throws Exception {
        // given
        PlanReviewResponseDto review = PlanReviewResponseDto.builder()
                .id("review-1")
                .userId(1L)
                .rating(5)
                .content("좋아요!")
                .createdAt(LocalDateTime.now())
                .build();

        when(planReviewService.getReviewsByPlanId("plan-1"))
                .thenReturn(Arrays.asList(review));

        // when & then
        mockMvc.perform(get("/api/plans/plan-1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].id").value("review-1"))
                .andExpect(jsonPath("$.data[0].rating").value(5))
                .andExpect(jsonPath("$.data[0].content").value("좋아요!"));
    }

    @Test
    @DisplayName("리뷰 목록 조회 - 빈 목록")
    void getReviews_empty() throws Exception {
        // given
        when(planReviewService.getReviewsByPlanId("plan-1"))
                .thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/plans/plan-1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("리뷰 등록 성공")
    void createReview_success() throws Exception {
        // given
        PlanReviewRequestDto requestDto = PlanReviewRequestDto.builder()
                .rating(5)
                .content("좋아요!")
                .build();

        // void 리턴 → Mockito는 doNothing() 필요 없음, 그냥 when().then() 없이 처리 가능
        // 테스트에서는 호출만 확인

        // when & then
        mockMvc.perform(post("/api/plans/plan-1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_success() throws Exception {
        // given
        var updateDto = com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewUpdateDto.builder()
                .rating(4)
                .content("수정된 내용")
                .build();

        // when & then
        mockMvc.perform(patch("/api/plans/plan-1/reviews/review-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview_success() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/plans/plan-1/reviews/review-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

}