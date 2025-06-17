package com.eureka.ip.team1.urjung_main.chatbot.utils;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonalAnalysisQuestionProvider {
    private final List<String> questions = List.of(
            "1. 평소에 데이터를 얼마나 자주 사용하시나요?\n(예: 하루에 1~2시간 정도 사용해요 / 월 20GB 정도 써요)",
            "2. 해외 로밍 서비스를 얼마나 자주 사용하시나요?\n(예: 해외 출장이 많아서 한 달에 한두 번 정도 써요 / 거의 사용하지 않아요)",
            "3. 주로 어떤 용도로 휴대폰을 사용하시나요?\n(예: SNS, 유튜브, 웹서핑, 업무용 메신저 등)"
    );

    public int total() {
        return questions.size();
    }

    public String getQuestion(int index) {
        return questions.get(index);
    }
}