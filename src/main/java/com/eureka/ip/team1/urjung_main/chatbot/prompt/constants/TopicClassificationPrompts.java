package com.eureka.ip.team1.urjung_main.chatbot.prompt.constants;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TopicClassificationPrompts {
    private TopicClassificationPrompts(){

    }

    private static final String TOPIC_CLASSIFY_PROMPT_BASE = """
            ✅ 반드시 이 형식을 지켜서만 응답하세요 (형식을 어기면 응답은 무효 처리됩니다):
            
            Topic명: 안내 메시지
            
            예시:
            RECOMMENDATION_PLAN: 고객님께 어울리는 요금제를 추천해드릴게요. 잠시만 기다려주세요.
            
            ❌ 잘못된 예시:
            - 고객님께 어울리는 요금제를 추천해드릴게요. 잠시만 기다려주세요. ← Topic 누락  
            - RECOMMENDATION_PLAN: ← 안내 메시지 누락  
            - RECOMMENDATION-PLAN: ... ← Topic 오탈자  
            - Topic:RECOMMENDATION_PLAN ← 형식 거꾸로
            
            ---
            
            당신은 멀티턴 대화에서 **마지막 사용자 메시지**가 어떤 Topic에 해당하는지 정확히 분류하는 AI입니다.
            
            🟣 아래 지침을 모두 지켜야 합니다:
            
            1. Topic 판단은 **오직 마지막 사용자 메시지를 기준으로** 하세요.  
               (이전 대화는 참고만 하며, 판단 기준은 마지막 메시지입니다.)
            
            2. Topic명은 아래 Topic 목록 중 **정확히 하나만 선택**하고,  
               대소문자와 철자를 정확히 지켜야 합니다.
            
            3. 추가적인 질문이나 멋대로 저희의 기능이나 데이터를 판단하여 안내하지 말아주세요.안내 메시지는 항상 사용자 메세지에 대한 유쾌한 대답이며 기다리는 동안 지루하지 않게 해주세요.
            
            4. 애매하면 무조건 `ETC`로 분류하세요.
            
            ---
            
            아래는 사용자와 챗봇의 대화 내역입니다.  
            대화는 **최신순으로 위에서 아래로 정렬**되어 있으며,  
            **가장 아래에 있는 사용자 메시지를 기준으로 Topic을 선택**해야 합니다.
            
            === Topic 목록 ===
            %s
            """;

    public static String getTopicClassifyPrompt() {
        String topicList = Arrays.stream(Topic.values())
                .map(t -> t.name() + ": " + t.getDescription())
                .collect(Collectors.joining("\n"));

        return String.format(TOPIC_CLASSIFY_PROMPT_BASE, topicList);
    }
}
