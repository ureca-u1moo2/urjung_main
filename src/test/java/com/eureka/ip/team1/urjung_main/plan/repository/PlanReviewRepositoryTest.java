package com.eureka.ip.team1.urjung_main.plan.repository;

import com.eureka.ip.team1.urjung_main.plan.entity.PlanReview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlanReviewRepositoryTest {

    @Autowired
    private PlanReviewRepository planReviewRepository;

    @Test
    @DisplayName("리뷰 저장 테스트")
    void saveReview() {
        // given
        PlanReview review = PlanReview.builder()
                .planId("plan-1")
                .userId("1")
                .rating(5)
                .content("리뷰 저장 테스트")
                .createdAt(LocalDateTime.now())
                .build();

        // when
        PlanReview savedReview = planReviewRepository.save(review);

        // then
        assertThat(savedReview.getId()).isNotNull();
        assertThat(savedReview.getPlanId()).isEqualTo("plan-1");
        assertThat(savedReview.getUserId()).isEqualTo("1");
        assertThat(savedReview.getRating()).isEqualTo(5);
        assertThat(savedReview.getContent()).isEqualTo("리뷰 저장 테스트");
    }

    @Test
    @DisplayName("planId로 리뷰 목록 조회")
    void findByPlanId() {
        // given
        PlanReview review1 = PlanReview.builder()
                .planId("plan-1")
                .userId("1")
                .rating(5)
                .content("리뷰1")
                .createdAt(LocalDateTime.now())
                .build();

        PlanReview review2 = PlanReview.builder()
                .planId("plan-1")
                .userId("2")
                .rating(4)
                .content("리뷰2")
                .createdAt(LocalDateTime.now())
                .build();

        planReviewRepository.save(review1);
        planReviewRepository.save(review2);

        // when
        List<PlanReview> reviews = planReviewRepository.findByPlanId("plan-1");

        // then
        assertThat(reviews).hasSize(2);
    }

    @Test
    @DisplayName("리뷰 id로 조회")
    void findById_success() {
        // given
        PlanReview review = PlanReview.builder()
                .planId("plan-1")
                .userId("1")
                .rating(5)
                .content("findById 테스트")
                .createdAt(LocalDateTime.now())
                .build();

        PlanReview savedReview = planReviewRepository.save(review);

        // when
        Optional<PlanReview> result = planReviewRepository.findById(savedReview.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getContent()).isEqualTo("findById 테스트");
    }

    @Test
    @DisplayName("리뷰 삭제 테스트")
    void deleteReview() {
        // given
        PlanReview review = PlanReview.builder()
                .planId("plan-1")
                .userId("1")
                .rating(3)
                .content("삭제 테스트")
                .createdAt(LocalDateTime.now())
                .build();

        PlanReview savedReview = planReviewRepository.save(review);

        // when
        planReviewRepository.delete(savedReview);

        // then
        Optional<PlanReview> result = planReviewRepository.findById(savedReview.getId());
        assertThat(result).isNotPresent();
    }
}
