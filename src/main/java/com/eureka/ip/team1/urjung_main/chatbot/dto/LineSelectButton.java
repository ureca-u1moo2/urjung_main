package com.eureka.ip.team1.urjung_main.chatbot.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LineSelectButton {
    private List<String> phoneNumbers;
}
