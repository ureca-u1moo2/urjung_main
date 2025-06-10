package com.eureka.ip.team1.urjung_main.chatbot.dto;


import com.eureka.ip.team1.urjung_main.chatbot.enums.CardType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Card<T> {
    private CardType type;
    private T value;
}
