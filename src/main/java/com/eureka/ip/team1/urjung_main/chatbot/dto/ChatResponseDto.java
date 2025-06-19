package com.eureka.ip.team1.urjung_main.chatbot.dto;

import com.eureka.ip.team1.urjung_main.chatbot.component.Button;
import com.eureka.ip.team1.urjung_main.chatbot.component.Card;
import com.eureka.ip.team1.urjung_main.chatbot.component.LineSelectButton;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
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
    private Topic topic;


    public static ChatResponseDto ofMainReply(String message, List<Card> cards, Topic topic) {
        return ChatResponseDto.builder()
                .type(ChatResponseType.MAIN_REPLY)
                .topic(topic)
                .message(message)
                .buttons(List.of(Button.planPage(), Button.recommendStart()))
                .cards(cards)
                .build();
    }

    public static ChatResponseDto ofAnalysisReply(String message) {
        return ChatResponseDto.builder()
                .type(ChatResponseType.ANALYSIS_REPLY)
                .buttons(List.of(Button.cancel()))
                .message(message)
                .build();
    }

    public static ChatResponseDto ofWaitingReply(String message) {
        return ChatResponseDto.builder()
                .type(ChatResponseType.WAITING)
                .message(message)
                .build();
    }

    public static ChatResponseDto ofInfoReply(String message, List<Button> buttons) {
        return ChatResponseDto.builder()
                .type(ChatResponseType.INFO)
                .buttons(buttons)
                .message(message)
                .build();
    }

    public static ChatResponseDto ofFeedBack(String message) {
        return ChatResponseDto.builder()
                .type(ChatResponseType.FEED_BACK)
                .message(message)
                .build();
    }
}

