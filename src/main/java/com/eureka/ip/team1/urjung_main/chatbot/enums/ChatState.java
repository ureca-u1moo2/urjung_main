package com.eureka.ip.team1.urjung_main.chatbot.enums;

public enum ChatState {
    IDLE,
    AWAITING_LINE_SELECTION,           // 회선 선택 대기
    AWAITING_PERSONAL_ANALYSIS_START,     // 성향 분석 대기
    PERSONAL_ANALYSIS,
    AWAITING_ADDITIONAL_FEEDBACK,
    RECOMMENDATION_START
}
