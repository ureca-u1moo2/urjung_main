package com.eureka.ip.team1.urjung_main.plan.service;
import com.eureka.ip.team1.urjung_main.common.exception.ForbiddenException;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewRequestDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewResponseDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewUpdateDto;
import com.eureka.ip.team1.urjung_main.plan.entity.PlanReview;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PlanReviewServiceTest {

    @Mock
    private PlanReviewRepository planReviewRepository;

    @InjectMocks
    private PlanReviewServiceImpl planReviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("리뷰 목록 조회 성공")
    void getReviews_success() {
        // given
        PlanReview review = PlanReview.builder()
                .id("review-1")
                .planId("plan-1")
                .userId(1L)
                .rating(5)
                .content("좋아요!")
                .createdAt(LocalDateTime.now())
                .build();

        when(planReviewRepository.findByPlanId("plan-1"))
                .thenReturn(Arrays.asList(review));

        // when
        List<PlanReviewResponseDto> reviews = planReviewService.getReviewsByPlanId("plan-1");

        // then
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getId()).isEqualTo("review-1");
    }

    @Test
    @DisplayName("리뷰 등록 성공")
    void createReview_success() {
        // given
        PlanReviewRequestDto requestDto = new PlanReviewRequestDto();
        requestDto.setRating(4);
        requestDto.setContent("괜찮아요");

        when(planReviewRepository.save(any(PlanReview.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PlanReviewResponseDto response = planReviewService.createReview("plan-1", 1L, requestDto);

        // then
        assertThat(response.getRating()).isEqualTo(4);
        assertThat(response.getContent()).isEqualTo("괜찮아요");
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_success() {
        // given
        PlanReview review = PlanReview.builder()
                .id("review-1")
                .planId("plan-1")
                .userId(1L)
                .rating(3)
                .content("이전 내용")
                .createdAt(LocalDateTime.now())
                .build();

        when(planReviewRepository.findById("review-1"))
                .thenReturn(Optional.of(review));

        PlanReviewUpdateDto updateDto = new PlanReviewUpdateDto();
        updateDto.setRating(5);
        updateDto.setContent("수정된 내용");

        // when
        planReviewService.updateReview("plan-1", "review-1", 1L, updateDto);

        // then
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("리뷰 수정 - 본인 아님 예외 발생")
    void updateReview_forbidden() {
        // given
        PlanReview review = PlanReview.builder()
                .id("review-1")
                .planId("plan-1")
                .userId(2L)  // 다른 사용자
                .rating(3)
                .content("이전 내용")
                .createdAt(LocalDateTime.now())
                .build();

        when(planReviewRepository.findById("review-1"))
                .thenReturn(Optional.of(review));

        PlanReviewUpdateDto updateDto = new PlanReviewUpdateDto();
        updateDto.setRating(5);
        updateDto.setContent("수정된 내용");

        // then
        assertThrows(ForbiddenException.class, () -> {
            planReviewService.updateReview("plan-1", "review-1", 1L, updateDto);  // 요청자 userId = 1L
        });
    }

    @Test
    @DisplayName("리뷰 수정 - 잘못된 요금제 ID 예외 발생")
    void updateReview_invalidPlanId() {
        // given
        PlanReview review = PlanReview.builder()
                .id("review-1")
                .planId("plan-1")
                .userId(1L)
                .rating(3)
                .content("이전 내용")
                .createdAt(LocalDateTime.now())
                .build();

        when(planReviewRepository.findById("review-1"))
                .thenReturn(Optional.of(review));

        PlanReviewUpdateDto updateDto = new PlanReviewUpdateDto();
        updateDto.setRating(5);
        updateDto.setContent("수정된 내용");

        // then
        assertThrows(com.eureka.ip.team1.urjung_main.common.exception.InvalidInputException.class, () -> {
            planReviewService.updateReview("wrong-plan-id", "review-1", 1L, updateDto);
        });
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview_success() {
        // given
        PlanReview review = PlanReview.builder()
                .id("review-1")
                .planId("plan-1")
                .userId(1L)
                .rating(5)
                .content("삭제할 리뷰")
                .createdAt(LocalDateTime.now())
                .build();

        when(planReviewRepository.findById("review-1"))
                .thenReturn(Optional.of(review));

        // when
        planReviewService.deleteReview("plan-1", "review-1", 1L);

        // then
        verify(planReviewRepository, times(1)).delete(review);
    }

    @Test
    @DisplayName("리뷰 삭제 - 본인 아님 예외 발생")
    void deleteReview_forbidden() {
        // given
        PlanReview review = PlanReview.builder()
                .id("review-1")
                .planId("plan-1")
                .userId(2L)  // 다른 사용자
                .rating(5)
                .content("삭제할 리뷰")
                .createdAt(LocalDateTime.now())
                .build();

        when(planReviewRepository.findById("review-1"))
                .thenReturn(Optional.of(review));

        // then
        assertThrows(ForbiddenException.class, () -> {
            planReviewService.deleteReview("plan-1", "review-1", 1L);  // 요청자 userId = 1L
        });
    }

    @Test
    @DisplayName("리뷰 삭제 - 잘못된 요금제 ID 예외 발생")
    void deleteReview_invalidPlanId() {
        // given
        PlanReview review = PlanReview.builder()
                .id("review-1")
                .planId("plan-1")
                .userId(1L)
                .rating(5)
                .content("삭제할 리뷰")
                .createdAt(LocalDateTime.now())
                .build();

        when(planReviewRepository.findById("review-1"))
                .thenReturn(Optional.of(review));

        // then
        assertThrows(com.eureka.ip.team1.urjung_main.common.exception.InvalidInputException.class, () -> {
            planReviewService.deleteReview("wrong-plan-id", "review-1", 1L);
        });
    }
}
