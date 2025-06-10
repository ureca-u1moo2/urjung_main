package com.eureka.ip.team1.urjung_main.chatbot.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ChatResponseDto {
    private String message;
    private List<Button> buttons;

    public void setButtons(List<Button> buttons){
        this.buttons = buttons;
    }
}

