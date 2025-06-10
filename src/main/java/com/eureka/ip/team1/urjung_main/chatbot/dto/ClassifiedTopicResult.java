package com.eureka.ip.team1.urjung_main.chatbot.dto;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClassifiedTopicResult {
    private Topic topic;
    private String waitMessage;
}
