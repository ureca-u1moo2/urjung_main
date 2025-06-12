package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.user.dto.LineSubscriptionDto;

public interface LineSubscriptionService {

    // 요금제 가입하기
    void subscribe(LineSubscriptionDto lineSubscriptionDto, String userId);

    // 요금제 해지하기
    void cancelLine(String userId, String lineId);

    // 요금제 할인 가격 조회
    int getDiscountedPrice(String userId, String planId);
}

