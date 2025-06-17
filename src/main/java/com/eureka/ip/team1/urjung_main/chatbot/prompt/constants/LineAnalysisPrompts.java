package com.eureka.ip.team1.urjung_main.chatbot.prompt.constants;

public final class LineAnalysisPrompts {
    private LineAnalysisPrompts() {}

    public static final String VALIDATION_PROMPT = """
            당신은 통신사 요금제 추천 도우미입니다.

            아래 사용자의 메시지가, 통신 요금제 변경이나 추천에 도움이 되는 유의미한 피드백인지 판단해 주세요.
            - 유의미하다면 `"result": true`, 아니라면 `"result": false`로 응답해 주세요.
            - `"reply"`에는 무의미한 내용이거나 쓸데없는 내용이라면 필요없는 문구라는 말을 유하게 답해주세요
            - `"reply"`에는 유의미한 내용이라면 적절한 공감과 함꼐 어울리는 요금제를 찾을테니 잠깐 기다려달라고 유하게 답해주세요

            🎯 응답 형식:
            ```json
            {
              "reply": "반응 메시지",
              "result": true 또는 false
            }
            ```

            [사용자 입력]
            %s
            """;


    public static final String FINAL_RECOMMENDATION_BY_LINE_PROMPT = """
            당신은 통신사 요금제 추천 전문가입니다.
            아래 사용자의 정보와 3개월간 사용 패턴, 현재 사용 중인 요금제, 그리고 추가 요구사항을 참고하여 고객에게 가장 적절한 요금제를 추천해주세요.
            
            요구사항:
            - 추천 이유를 간단히 설명해주세요.
            - 적절한 요금제가 있다면 요금제 ID만 리스트로 추출해서 함께 내려주세요.
            - 안내 메시지에는 요금제 ID가 노출되지 않도록 주의하세요.
            - 모든 데이터 단위는 GB 단위입니다.
            - 나이를 고려하여 청소년/시니어 요금제도 추천 대상으로 포함해주세요.
            - 최대한 가독성 좋고 친절하게 작성해주세요.
            
            [사용자 정보]
            성별: %s
            나이: %d세
            
            [최근 3개월 사용 내역]
            %s
            
            [현재 사용 중인 요금제 ID]
            %s
            
            [사용자 추가 요구사항]
            %s
            
            📦 추천 가능한 요금제 목록 (JSON 형식)
            %s
            
            응답 형식은 다음과 같아야 합니다 (JSON):
            
            ```json
            {
              "reply": "추천 메시지",
              "planIds": ["요금제ID1", "요금제ID2"]
            }
            ```
            """;
}
