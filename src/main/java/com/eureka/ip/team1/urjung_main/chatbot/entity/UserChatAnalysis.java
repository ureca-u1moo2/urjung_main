package com.eureka.ip.team1.urjung_main.chatbot.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("chat_analysis")
public class UserChatAnalysis {

    @Id
    private String sessionId;

    private String userId;

    private int currentStep;


    private Map<Integer, String> answers = new HashMap<>();

    public void addAnswer(String answer) {
        if (answers == null) {
            answers = new HashMap<>();
        }
        answers.put(currentStep, answer);
        currentStep++; // 다음 단계로 전진
    }
}
