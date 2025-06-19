package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanCompareAiResponseDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanCompareAiServiceImpl implements PlanCompareAiService {

    private final PlanRepository planRepository;
    private final GeminiApiClient geminiApiClient;

    @Override
    public PlanCompareAiResponseDto analyzeComparison(List<String> planIds) {
        List<Plan> plans = planRepository.findAllById(planIds);

        if (plans.size() != planIds.size()) {
            throw new IllegalArgumentException("일부 요금제를 찾을 수 없습니다.");
        }

        // 1. 프롬프트 구성
        StringBuilder prompt = new StringBuilder("""
                다음은 여러 요금제 정보입니다.
                
                각 요금제를 귀엽고 부드러운 요플레 느낌으로 비교해주세요 🍓
                
                출력 형태 예시는 다음과 같아요 다음 형식을 고정해주세요:
                ① 요약 비교 (모든 요금제 작성 필요)
                요금제 A는 가성비가 좋고 B는 데이터가 많아요(비교 개수 만큼 내용 요약C, D, E 등등) 🍓
                
                ② 장점과 단점
                [A요금제]
                - 장점 : 가격이 저렴해요!
                - 단점 : 통화량이 적어요.
                
                [B요금제]
                - 장점 : 통화 무제한 이에요!
                - 단점 : 가격이 높아요!
                
                ③ 추천 사용자 (로그인 여부 상관 없이 모든 요금제에 대해 꼭 추천 대상을 알려주세요!)
                요금제 이름 : 추천 사용자 설명
                
                예시 :
                슬림요금제 : 통화량이 적은 시니어 !
                무제한 데이터 요금제 : 인강을 많이 보는 학생
                가성비 요금제 : 요금 절약이 필요한 사람
                언제나 통화 요금제 : 통화량 많은 직장인!
                (추천 사용자의 경우, 요금제 정보가 부족한 경우 요금제 장점의 특징을 적어주세요.)
                
                
                출력 항목은 아래와 같아요:
                
                1. 요약 비교 (10자 내외로 짧고 귀엽게!)
                2. 각 요금제의 장점 / 단점 (줄글 대신 간단히 핵심만!)
                3. 추천 사용자 유형 (너무 길지 않게 짧게 설명해줘요)
                모든 내용은 존댓말로 해주세요!
               
                
                참고: 마크다운 기호(**, *, # 등)는 사용하지 말아주세요!
                
                👇 요금제 정보입니다:
                """);

        // 꼭 추가

        for (Plan plan : plans) {
            String tagNames = plan.getTags().stream()
                    .map(tag -> tag.getTagName()) // 람다로 변경
                    .collect(Collectors.joining(", ")); // 문자열로 연결

            prompt.append("📦 이름: ").append(plan.getName()).append("\n")
                    .append("💰 가격: ").append(plan.getPrice()).append("원\n")
                    .append("📶 데이터: ").append(
                            plan.getDataAmount() == -1 ? "무제한"
                                    : (plan.getDataAmount() >= 1024
                                    ? (plan.getDataAmount() / 1024) + "GB"
                                    : plan.getDataAmount() + "MB")
                    ).append("\n")
                    .append("📞 통화: ").append(plan.getCallAmount()).append("분\n")
                    .append("💬 문자: ").append(plan.getSmsAmount()).append("건\n")
                    .append("🔖 태그: ").append(tagNames).append("\n\n");
        }

        // 2. Gemini API 호출
        String aiSummary = geminiApiClient.getGeminiSummary("요금제 비교 분석", prompt.toString());

        // 3. 응답용 DTO 구성
        List<PlanDetailDto> planDtos = plans.stream()
                .map(p -> PlanDetailDto.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .price(p.getPrice())
                        .description(p.getDescription())
                        .dataAmount(p.getDataAmount())
                        .callAmount(p.getCallAmount())
                        .smsAmount(p.getSmsAmount())
                        .createdAt(p.getCreatedAt())
                        .tags(p.getTags())
                        .build())
                .toList();

        return PlanCompareAiResponseDto.builder()
                .plans(planDtos)
                .aiSummary(aiSummary)
                .build();
    }
}
