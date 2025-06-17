package com.eureka.ip.team1.urjung_main.chatbot.dto;

import com.eureka.ip.team1.urjung_main.chatbot.component.Button;
import com.eureka.ip.team1.urjung_main.chatbot.component.Card;
import com.eureka.ip.team1.urjung_main.chatbot.component.LineSelectButton;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ChatResponseDto {
    private ChatResponseType type;
    private String message;
    private List<Button> buttons;
    private List<Card> cards;
    private LineSelectButton lineSelectButton;
}

