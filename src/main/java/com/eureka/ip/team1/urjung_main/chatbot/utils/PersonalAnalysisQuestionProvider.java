package com.eureka.ip.team1.urjung_main.chatbot.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class PersonalAnalysisQuestionProvider {
    private final List<String> questions = List.of(
            "**1. 한 달에 데이터를 얼마나 사용하시나요?**\n하루 평균 사용 시간이나 한 달 기준 데이터 사용량을 알려주시면 좋아요!\n(예: 하루 1~2시간 / 월 5GB / 유튜브 자주 봐요)",

            "**2. 통화는 얼마나 하시나요?**\n하루 또는 한 달 기준으로 얼마나 통화하시는지, 무제한이 필요한지도 함께 알려주세요!\n(예: 하루에 10분 정도 / 전화 통화를 자주 해요)",

            "**3. 문자 메시지는 얼마나 사용하시나요?**\n카카오톡 말고 문자(SMS)를 얼마나 자주 쓰시는지 궁금해요.\n(예: 거의 안 써요 / 업무상 자주 사용해요)",

            "**4. 주로 어떤 용도로 휴대폰을 사용하시나요?**\n영상 시청, SNS, 게임 등 어떤 활동을 가장 많이 하시는지 알려주세요!\n(예: SNS / 유튜브 / 업무용 메신저 / 웹서핑)",

            "**5. 요금제에서 가장 중요하게 생각하는 부분은 무엇인가요?**\n가격, 데이터, 통화 중 어떤 요소를 가장 중요하게 생각하시나요?\n(예: 가격 / 데이터 제공량 / 통화 무제한 )"
    );

    public int total() {
        return questions.size();
    }

    public String getQuestion(int index) {
        return questions.get(index);
    }
}