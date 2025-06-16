package com.eureka.ip.team1.urjung_main.chatbot.enums;

public enum ChatState {
    DEFAULT,
    WAITING_SELECT_LINE,           // 회선 선택 대기
    LINE_USAGE_ANALYSIS,           // 선택한 회선 사용 이력 분석
    WAITING_PERSONAL_ANALYSIS,     // 성향 분석 대기
    PERSONAL_ANALYSIS_1,           // 성향 분석 질문 1
    PERSONAL_ANALYSIS_2,
    PERSONAL_ANALYSIS_3,
    WAITING_INPUT_NEED, COMPLETE_ANALYSIS
    // ....
}
