package com.eureka.ip.team1.urjung_main.chatbot.dto;

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

    /**
     * "성향 분석 시작" 버튼 생성
     */
    public static Button analysisStart() {
        return Button.builder()
                .label("성향 분석 진행")
                .value("성향 분석 시작")
                .type(ButtonType.INPUT_DATA)
                .build();
    }

    /**
     * "취소" 버튼 생성
     */
    public static Button cancel() {
        return Button.builder()
                .label("취소")
                .value("취소")
                .type(ButtonType.INPUT_DATA)
                .build();
    }

    /**
     * 외부 URL 이동 버튼 생성
     */
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
