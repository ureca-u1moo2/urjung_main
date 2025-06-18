package com.eureka.ip.team1.urjung_main.chatbot.prompt.constants;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TopicClassificationPrompts {
    private TopicClassificationPrompts() {

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
            
            🟣 아래 분류 기준을 따라야 합니다:
            
            1. Topic 판단은 **오직 마지막 사용자 메시지**만 기준으로 합니다.  
               (이전 대화는 참고만 하되, 판단은 마지막 메시지로만 해야 합니다.)
            
            2. Topic명은 아래 목록 중 **정확히 하나만 선택**해야 하며,  
               대소문자나 철자 오탈자가 있으면 안 됩니다.
            
            3. 안내 메시지는 반드시 **자연스럽고 긍정적인 말투로**,  
               "잠시만 기다려주세요" 등의 표현을 포함하여 응답을 마무리하세요.  
               질문, 설명, 홍보 등은 절대 하지 마세요.
            
            4. **특정 단어가 있다고 해서 Topic을 판단하지 마세요.**  
               예를 들어 "추천", "비교", "알려줘" 같은 단어는 문맥에 따라 다양한 Topic이 될 수 있습니다.  
               문장의 **의도와 목적**이 무엇인지 중심으로 판단하세요.
            
               - 예: “추천해줘” → 조건이 없다면 `RECOMMENDATION_PLAN`,  
                 조건이 붙는다면 `PLAN_LIST` (예: “무제한 요금제 추천해줘”)
            
               - 예: “Z플랜 어떤 거야?” → 특정 요금제 → `PLAN_DETAIL`  
                 “요금제 뭐 있어?” → 전체 목록 요청 → `ALL_PLAN_LIST`
            
            5. 판단이 애매하거나 Topic에 딱 맞지 않는 경우 → `ETC`로 분류하세요.
            
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
