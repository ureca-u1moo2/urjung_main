package com.eureka.ip.team1.urjung_main.chatbot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Topic {
    INFO("서비스 사용법에 관한 내용"),
    ALL_PLAN_INFORMATION("전체 요금제에 관한 내용"),
    PLAN_DETAIL("하나의 요금제에 관한 내용"),
    RECOMMENDATION_PLAN("요금제 추천에 관한 내용"),
    COMPARE_PLAN_WITH_MY_PLAN("내가 사용중인 요금제와 다른 요금제 비교"),
    COMPARE_PLAN_WITHOUT_MY_PLAN("내가 사용중인 요금제가 아닌 다른 요금제끼리의 비교"),
    MY_USAGE_INFORMATION("사용자의 이용내역에 관한 내용"),
    ETC("그 외 우리 서비스와 관련 없는 내용");
    // 사용자 이용내역에 대한 내용

    private final String description;
}
