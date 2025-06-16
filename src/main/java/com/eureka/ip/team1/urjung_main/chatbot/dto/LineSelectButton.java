package com.eureka.ip.team1.urjung_main.chatbot.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class LineSelectButton {
    private List<String> phoneNumbers;

    public static LineSelectButton of(List<String> phoneNumbers) {
        return LineSelectButton.builder()
                .phoneNumbers(phoneNumbers != null ? phoneNumbers : Collections.emptyList())
                .build();
    }

    public static LineSelectButton empty() {
        return LineSelectButton.builder()
                .phoneNumbers(Collections.emptyList())
                .build();
    }
}
