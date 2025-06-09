package com.eureka.ip.team1.urjung_main.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRequestDto {
    private String message;
    private String sessionId;
    private String prompt;

    public ChatRequestDto withMessage(String prompt) {
        this.prompt = prompt;
        return this;
    }
}
