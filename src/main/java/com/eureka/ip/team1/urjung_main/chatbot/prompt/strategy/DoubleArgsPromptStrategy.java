package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

public interface DoubleArgsPromptStrategy<T,U> extends PromptStrategy{
    String generatePrompt(T data1, U data2);
}
