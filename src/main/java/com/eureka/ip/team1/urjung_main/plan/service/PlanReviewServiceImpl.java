package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.common.exception.ForbiddenException;
import com.eureka.ip.team1.urjung_main.common.exception.InvalidInputException;
import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewRequestDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewResponseDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanReviewUpdateDto;
import com.eureka.ip.team1.urjung_main.plan.entity.PlanReview;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// 요금제 리뷰 ServiceImpl

@Service
@RequiredArgsConstructor
public class PlanReviewServiceImpl implements PlanReviewService {

    private final PlanReviewRepository planReviewRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // 요금제 리뷰 목록
    @Override
    public List<PlanReviewResponseDto> getReviewsByPlanId(String planId) {
        List<PlanReview> reviews = planReviewRepository.findByPlanId(planId);

        return reviews.stream()
                .map(review -> PlanReviewResponseDto.builder()
                        .id(review.getId())
                        .userId(review.getUserId())
                        .rating(review.getRating())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 요금제 리뷰 등록
    @Override
    @Transactional
    public PlanReviewResponseDto createReview(String planId, String userId, PlanReviewRequestDto requestDto) {
        PlanReview review = PlanReview.builder()
                .planId(planId)
                .userId(userId)
                .rating(requestDto.getRating())
                .content(requestDto.getContent())
                .build();

        PlanReview savedReview = planReviewRepository.save(review);

        // 저장 후 → PlanReviewResponseDto 로 변환해서 리턴
        return PlanReviewResponseDto.builder()
                .id(savedReview.getId())
                .userId(savedReview.getUserId())
                .rating(savedReview.getRating())
                .content(savedReview.getContent())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }

    // 요금제 리뷰 수정
    @Override
    @Transactional
    public void updateReview(String planId, String reviewId, String userId, PlanReviewUpdateDto updateDto) {
        PlanReview review = planReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다"));

        if (!review.getUserId().equals(userId)) {
            throw new ForbiddenException("본인만 수정할 수 있습니다");
        }

        if (!review.getPlanId().equals(planId)) {
            throw new InvalidInputException("잘못된 요금제 ID입니다");
        }

        // 수정 적용
        review.setRating(updateDto.getRating());
        review.setContent(updateDto.getContent());
    }

    // 요금제 리뷰 삭제
    @Override
    @Transactional
    public void deleteReview(String planId, String reviewId, String userId) {
        PlanReview review = planReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다"));

        if (!review.getUserId().equals(userId)) {
            throw new ForbiddenException("본인만 삭제할 수 있습니다");
        }

        if (!review.getPlanId().equals(planId)) {
            throw new InvalidInputException("잘못된 요금제 ID입니다");
        }

        planReviewRepository.delete(review);
    }



}
