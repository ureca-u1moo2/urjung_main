package com.eureka.ip.team1.urjung_main.chatbot.service;

public interface ForbiddenWordService {
    boolean containsForbiddenWord(String text);
    String censor (String text);
}
