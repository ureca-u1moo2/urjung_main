package com.eureka.ip.team1.urjung_main.chatbot.utils;

import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.*;

public class PromptStrategyInvoker {

    public static String invokeSingleArgStrategy(PromptStrategy strategy, String arg) {
        if (strategy instanceof SingleArgsPromptStrategy singleStrategy) {
            return singleStrategy.generatePrompt(arg);
        }
        throw new ClassCastException("SingleArgsPromptStrategy 타입이 아님");
    }

    public static String invokeNoArgsStrategy(PromptStrategy strategy) {
        if (strategy instanceof NoArgsPromptStrategy noArgsStrategy) {
            return noArgsStrategy.generatePrompt();
        }
        throw new ClassCastException("NoArgsPromptStrategy 타입이 아님");
    }
}
