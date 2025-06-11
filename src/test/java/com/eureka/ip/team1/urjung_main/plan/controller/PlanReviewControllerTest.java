package com.eureka.ip.team1.urjung_main.plan.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewRequestDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewResponseDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewUpdateDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanReviewService;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlanReviewController.class)
@WithMockUser(username = "test@example.com", roles = "USER")
public class PlanReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanReviewService planReviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String planId = "plan-1";
    private final String reviewId = "review-1";
    private final String userId = "user-1";

    private void setAuthentication() {
        User mockUser = User.builder()
                .userId("user-1")
                .email("test@example.com")
                .password("testpass")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockUser, List.of());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("리뷰 목록 조회")
    void getReviewsByPlanId() throws Exception {
        List<PlanReviewResponseDto> mockList = List.of(
                PlanReviewResponseDto.builder()
                        .id("r1")
                        .userId("user-1")
                        .rating(4)
                        .content("좋아요")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        when(planReviewService.getReviewsByPlanId(planId)).thenReturn(mockList);

        mockMvc.perform(get("/api/plans/{planId}/reviews", planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].content").value("좋아요"));
    }

    @Test
    @DisplayName("리뷰 등록")
    void createReview() throws Exception {
        setAuthentication();

        PlanReviewRequestDto requestDto = PlanReviewRequestDto.builder()
                .rating(5)
                .content("아주 좋음")
                .build();

        when(planReviewService.createReview(any(), any(), any())).thenReturn(null);

        mockMvc.perform(post("/api/plans/{planId}/reviews", planId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    @DisplayName("리뷰 수정")
    void updateReview() throws Exception {
        setAuthentication();

        PlanReviewUpdateDto updateDto = PlanReviewUpdateDto.builder()
                .rating(3)
                .content("수정된 내용")
                .build();

        doNothing().when(planReviewService).updateReview(any(), any(), any(), any());

        mockMvc.perform(patch("/api/plans/{planId}/reviews/{reviewId}", planId, reviewId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    @DisplayName("리뷰 삭제")
    void deleteReview() throws Exception {
        setAuthentication();

        doNothing().when(planReviewService).deleteReview(any(), any(), any());

        mockMvc.perform(delete("/api/plans/{planId}/reviews/{reviewId}", planId, reviewId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }
}
