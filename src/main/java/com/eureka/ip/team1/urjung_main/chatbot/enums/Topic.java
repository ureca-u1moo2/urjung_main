package com.eureka.ip.team1.urjung_main.chatbot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Topic {
    INFO("서비스 사용법에 관한 내용입니다. 예: '이 앱은 어떻게 사용하는 거예요?', '요금제 추천은 어떻게 받아요?'"),
    PLAN_DETAIL("하나의 요금제에 대한 상세 정보를 요청하는 내용입니다. 예: 'Z플랜 어떤 거야?', '프리미엄 요금제 설명해줘'"),
    RECOMMENDATION_PLAN("조건 없이 추천을 요청하는 문장입니다. 예: '추천해줘', '내게 맞는 요금제 알려줘'. 조건이 조금이라도 포함된 문장은 PLAN_LIST로 분류해야 합니다. 예: '무제한 요금제 추천해줘', '데이터 많은 요금제 추천해줘'는 PLAN_LIST입니다."),
    COMPARE_PLAN("내 요금제를 제외한 두 개 이상의 요금제 간의 차이점을 비교해달라는 요청입니다. 예: 'A 요금제랑 B 요금제 비교해줘'"),
    COMPARE_WITH_MY_PLAN("내 요금제를 포함한 두 개 이상의 요금제 간의 차이점을 비교해달라는 요청입니다. 예: '내 요금제랑 A 요금제 비교해줘'"),
    MY_USAGE_INFORMATION("사용자의 데이터, 통화, 문자 사용 내역 등을 확인하는 요청입니다. 예: '이번 달 데이터 얼마나 썼어?'"),
    PLAN_LIST("전체 요금제 또는 특정 조건(5G, 무제한, 청소년용 등)에 해당하는 요금제 목록을 보여달라는 요청입니다. 예: '전체 요금제 보여줘', '5G 요금제 뭐 있어?', '무제한 요금제 알려줘'"),
    ETC("요금제나 서비스와 관련 없는 기타 질문입니다. 분류되지 않으면 여기로 넣어주세요. 예: '오늘 날씨 어때?', 'AI는 똑똑하네', '심심해'");

    private final String description;
}
