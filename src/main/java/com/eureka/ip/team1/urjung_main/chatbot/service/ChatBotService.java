package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;

public interface ChatBotService {
    ChatResponseDto handleUserMessage(String userId, ChatRequestDto chatRequestDto);
}
