package com.eureka.ip.team1.urjung_main.chatbot.dto;

import com.eureka.ip.team1.urjung_main.chatbot.component.Button;
import com.eureka.ip.team1.urjung_main.chatbot.component.Card;
import com.eureka.ip.team1.urjung_main.chatbot.component.LineSelectButton;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder(builderClassName = "CustomBuilder")
@Getter
public class ChatResponseDto {
    private ChatResponseType type;
    private String message;
    private List<Button> buttons;
    private List<Card> cards;
    private LineSelectButton lineSelectButton;

    public static class CustomBuilder {
        private ChatResponseType type;
        private String message;
        private List<Button> buttons;
        private List<Card> cards;
        private LineSelectButton lineSelectButton;

        public ChatResponseDto build() {
            if (buttons == null) {
                buttons = new ArrayList<>();
                if (this.type == ChatResponseType.MAIN_REPLY) {
                    buttons.add(Button.planPage());
                    buttons.add(Button.recommendStart());
                }

                if (this.type == ChatResponseType.ANALYSIS_REPLY) {
                    buttons.add(Button.cancel());
                }
            }
            return new ChatResponseDto(type, message, buttons, cards, lineSelectButton);
        }
    }

    // 생성자 추가
    private ChatResponseDto(ChatResponseType type, String message, List<Button> buttons,
                            List<Card> cards, LineSelectButton lineSelectButton) {
        this.type = type;
        this.message = message;
        this.buttons = buttons;
        this.cards = cards;
        this.lineSelectButton = lineSelectButton;
    }
}

