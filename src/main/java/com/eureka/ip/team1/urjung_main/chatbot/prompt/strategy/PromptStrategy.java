package com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;

import java.util.Optional;

public interface PromptStrategy {

    //    public String generatePrompt() {
//        throw new UnsupportedOperationException("기본 프롬프트 생성은 지원되지 않습니다.");
//    }
//
//    public String generatePrompt(T systemData) {
//        throw new UnsupportedOperationException("단일 데이터 기반 프롬프트 생성을 지원하지 않습니다.");
//    }
//
//    public String generatePrompt(T systemData, T userData) {
//        throw new UnsupportedOperationException("복수 데이터 기반 프롬프트 생성을 지원하지 않습니다.");
//    }
//
//    public abstract
    Optional<Topic> support();
}

