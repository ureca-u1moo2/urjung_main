package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.ChatResponseDto;

public interface ChatBotService {
    ChatResponseDto handleUserMessage(String userId, ChatRequestDto chatRequestDto);
}
