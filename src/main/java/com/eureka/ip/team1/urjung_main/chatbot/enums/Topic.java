package com.eureka.ip.team1.urjung_main.chatbot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Topic {
    INFO("서비스 사용법에 관한 내용입니다. 예: '이 앱은 어떻게 사용하는 거예요?', '요금제 추천은 어떻게 받아요?'"),
    ALL_PLAN_INFORMATION("전체 요금제 목록을 조건 없이 보여달라는 요청입니다. 예: '전체 요금제 알려줘', '요금제 종류 다 보여줘'"),
    PLAN_DETAIL("하나의 요금제에 대한 상세 정보를 요청하는 내용입니다. 예: 'Z플랜 어떤 거야?', '프리미엄 요금제 설명해줘'"),
    RECOMMENDATION_PLAN("특정 조건을 입력하지 않은 사용자에게 적합한 요금제를 추천해달라는 요청입니다. 예: '추천해줘', '내게 맞는 요금제는 뭐야?'"),
    COMPARE_PLAN_WITH_MY_PLAN("내가 사용 중인 요금제와 다른 요금제를 비교해달라는 요청입니다. 예: '내 요금제랑 S플랜 비교해줘'"),
    COMPARE_PLAN_WITHOUT_MY_PLAN("내가 쓰는 건 제외하고, 서로 다른 요금제를 비교해달라는 요청입니다. 예: 'A 요금제랑 B 요금제 비교해줘'"),
    MY_USAGE_INFORMATION("사용자의 데이터, 통화, 문자 사용 내역 등을 확인하는 요청입니다. 예: '이번 달 데이터 얼마나 썼어?'"),
    FILTERED_PLAN_LIST("전체 요금제 중 특정 조건(무제한, 5G, 저가, 랜덤, 순위 등)을 만족하거나 사용자가 말하는 성향에 어울리는 요금제를 보여달라는 요청입니다. 예: '무제한 요금제 뭐 있어?', '5G 요금제만 알려줘'"),
    ETC("요금제나 서비스와 관련 없는 기타 질문입니다. 분류되지 않으면 여기로 넣어주세요. 예: '오늘 날씨 어때?', 'AI는 똑똑하네', '심심해'");

    private final String description;
}
