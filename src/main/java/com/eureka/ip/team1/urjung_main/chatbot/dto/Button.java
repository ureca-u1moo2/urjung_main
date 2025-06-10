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
}