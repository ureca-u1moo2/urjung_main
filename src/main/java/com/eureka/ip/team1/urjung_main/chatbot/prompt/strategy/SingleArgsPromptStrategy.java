package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

public interface SingleArgsPromptStrategy<T> extends PromptStrategy{
    String generatePrompt(T data);
}
