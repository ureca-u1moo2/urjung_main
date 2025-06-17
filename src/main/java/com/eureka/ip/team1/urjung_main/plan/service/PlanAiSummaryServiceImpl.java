package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanAiSummaryResponseDto;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.entity.PlanReview;
import com.eureka.ip.team1.urjung_main.plan.entity.PlanSummary;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanReviewRepository;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PlanAiSummaryServiceImpl implements PlanAiSummaryService {

    private final PlanReviewRepository planReviewRepository;
    private final PlanRepository planRepository;
    private final PlanSummaryRepository planSummaryRepository;
    private final GeminiApiClient geminiApiClient;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public PlanAiSummaryResponseDto summarizePlanReview(String planId) {
        // Redis key 구성
        String redisKey = "plan:summary:" + planId;

        // 1. Redis에서 캐시 조회
        String cachedSummary = redisTemplate.opsForValue().get(redisKey);
        if (cachedSummary != null) {
            return new PlanAiSummaryResponseDto(cachedSummary);
        }

        // plan 조회
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요금제를 찾을 수 없습니다: " + planId));

//        // 기존 요약 찾기
//        Optional<PlanSummary> optionalSummary = planSummaryRepository.findByPlan(plan);
//        PlanSummary planSummary = optionalSummary.orElse(null);
//
//        // 24시간 이내이면 기존 요약 반환
//        if (planSummary != null && planSummary.getUpdatedAt() != null) {
//            Duration duration = Duration.between(planSummary.getUpdatedAt(), LocalDateTime.now());
//            if (duration.toHours() < 24) {
//                return new PlanAiSummaryResponseDto(planSummary.getSummaryText());
//            }
//        }

        // 리뷰 수집
        List<PlanReview> reviews = planReviewRepository.findByPlanId(planId);
        if (reviews.isEmpty()) {
            return new PlanAiSummaryResponseDto("리뷰가 없어 요약할 수 없습니다.");
        }

        StringBuilder sb = new StringBuilder();
        for (PlanReview review : reviews) {
            sb.append("- ").append(review.getContent()).append("\n");
        }

        String prompt = """
                다음은 특정 요금제에 대한 사용자 리뷰입니다.

                💡 아래 정보를 30자 이내로 간결하게 요약해 주세요.
                💖 요플레(핑크) 컨셉에 어울리는 귀엽고 부드러운 말투를 사용해주세요!

                📌 출력 형식은 다음과 같이 정확하게 지켜주세요 (항목마다 줄바꿈):
                1. 평점 평균: (숫자만 표시, 예: 4.3)
                2. 장점: (30자 이내, 말투 적용)
                3. 단점: (30자 이내, 말투 적용)
                4. 종합 평가: (30자 이내, 말투 적용)

                👇 아래는 사용자 리뷰입니다:
                """ + sb;

        String summaryText = geminiApiClient.getGeminiSummary("요금제 리뷰 요약", prompt);

        // 3. Redis에 24시간 TTL로 캐싱
        redisTemplate.opsForValue().set(redisKey, summaryText, Duration.ofHours(24));

        // 4. DB에도 백업 저장
        PlanSummary planSummary = planSummaryRepository.findByPlan(plan).orElse(null);
        if (planSummary == null) {
            planSummary = PlanSummary.builder()
                    .id(UUID.randomUUID().toString())
                    .plan(plan)
                    .build();
        }

        planSummary.setSummaryText(summaryText);
        planSummary.setUpdatedAt(LocalDateTime.now());
        planSummaryRepository.save(planSummary);

        return new PlanAiSummaryResponseDto(summaryText);
    }
}