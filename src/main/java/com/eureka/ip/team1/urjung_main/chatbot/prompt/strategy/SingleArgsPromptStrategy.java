package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

public interface SingleArgsPromptStrategy extends PromptStrategy{
    String generatePrompt(String data);
}
