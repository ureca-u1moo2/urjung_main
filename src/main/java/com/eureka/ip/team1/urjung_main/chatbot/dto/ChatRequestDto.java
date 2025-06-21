package com.eureka.ip.team1.urjung_main.chatbot.dto;

import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatRequestDto {
    private String message;
    private String sessionId;
    private List<String> planIds;
    private ChatCommand command;
}
