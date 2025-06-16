package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanAiSummaryResponseDto;
import com.eureka.ip.team1.urjung_main.plan.entity.PlanReview;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanAiSummaryServiceImpl implements PlanAiSummaryService {

    private final PlanReviewRepository planReviewRepository;
    private final GeminiApiClient geminiApiClient;

    @Override
    public PlanAiSummaryResponseDto summarizePlanReview(String planId) {
        List<PlanReview> reviews = planReviewRepository.findByPlanId(planId);

        if (reviews.isEmpty()) {
            return new PlanAiSummaryResponseDto("리뷰가 존재하지 않아 요약할 수 없습니다.");
        }

        StringBuilder sb = new StringBuilder();
        for (PlanReview review : reviews) {
            sb.append("- ").append(review.getContent()).append("\n");
        }

        String prompt = """
                다음은 특정 요금제에 대한 사용자 리뷰입니다. 
                
           
                리뷰마다 평점이 있습니다. 평점 평균 부분에는 리뷰마다의 평점의 평균을 구해서 숫자로 보여주세요.
                해당 사이트의 컨셉은 요플레, 핑크 입니다. 따라서 해당 컨셉에 알맞은 말투 부탁드려요.
                
                "각 항목은 줄바꿈(\\\\n)으로 구분해서 출력해 주세요.\\n\\n1. 장점: ... "
               
                짧게  요약 해주세요. 각 항목별 30자 이내로 요약해주세요.
                1.평점 평균
                2.장점
                3.단점
                4.종합 평가
                
                아래는 리뷰 내용입니다. 
                """ + sb;

        String summary = geminiApiClient.getGeminiSummary("요금제 리뷰 요약", prompt);
        return new PlanAiSummaryResponseDto(summary);
    }
}

