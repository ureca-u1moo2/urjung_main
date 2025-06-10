package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class ServiceInfoPromptStrategy implements NoArgsPromptStrategy{
    private static final String SYSTEM_INFO_BASE_PROMPT =
            "우리 서비스의 사용 방법에 대해 사용자에게 설명해주세요.\n" +
            "\n" +
            "서비스는 LG U+의 다양한 요금제를 추천, 비교, 조회할 수 있는 챗봇 기반 플랫폼입니다.  \n" +
            "사용자는 다음과 같은 방식으로 서비스를 이용할 수 있습니다:\n" +
            "\n" +
            "1. 챗봇에게 질문을 입력하여 요금제를 추천받거나, 요금제 정보를 확인하거나, 요금제 간 비교를 요청할 수 있습니다.\n" +
            "2. 로그인한 사용자는 자신의 통신 사용 내역을 기반으로 개인화된 요금제 추천을 받을 수 있습니다.\n" +
            "3. 마이페이지 또는 요금제 페이지에서도 요금제 상세 정보를 확인할 수 있습니다.\n" +
            "\n" +
            "이 정보를 사용자에게 이해하기 쉽고 친절하게 안내해 주세요.  \n" +
            "문장은 부드럽고 자연스럽게 구성하고, 필요한 경우 번호 목록을 활용하세요.\n";
    @Override
    public String generatePrompt() {
        return SYSTEM_INFO_BASE_PROMPT;
    }

    @Override
    public Optional<Topic> support() {
        return Optional.of(Topic.INFO);
    }


}
