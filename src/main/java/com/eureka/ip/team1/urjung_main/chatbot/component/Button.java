package com.eureka.ip.team1.urjung_main.chatbot.component;

import com.eureka.ip.team1.urjung_main.chatbot.enums.ButtonType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Button {
    private String label;  // 버튼에 표시할 텍스트
    private ButtonType type;   // 예: "url", "route", "event"
    private String value;  // URL 또는 내부 라우트 경로

    // ✅ 자주 쓰는 버튼 정적 생성 메서드들

    public static Button recommendStart() {
        return Button.builder()
                .label("내게 맞는 요금제 찾기")
                .value("http://localhost:8080/api/chat/recommend/start")
                .type(ButtonType.EVENT)
                .build();
    }

    public static Button personalAnalysisStart() {
        return Button.builder()
                .label("성향 분석 하기")
                .value("http://localhost:8080/api/chat/analysis/start")
                .type(ButtonType.EVENT)
                .build();
    }


    public static Button cancel() {
        return Button.builder()
                .label("요금제 추천 모드 종료")
                .value("http://localhost:8080/api/chat/state/default")
                .type(ButtonType.EVENT)
                .build();
    }

    public static Button planPage(){
        return Button.builder()
                .label("요금제 페이지로 이동")
                .value("http://localhost:3000/plans")
                .type(ButtonType.URL)
                .build();
    }

    public static Button url(String label, String link) {
        return Button.builder()
                .label(label)
                .value(link)
                .type(ButtonType.URL)
                .build();
    }

    /**
     * 내부 라우팅 또는 명령 트리거 버튼 생성
     */
    public static Button input(String label, String value) {
        return Button.builder()
                .label(label)
                .value(value)
                .type(ButtonType.INPUT_DATA)
                .build();
    }
}
