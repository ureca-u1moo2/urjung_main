package com.eureka.ip.team1.urjung_main.chatbot.facade;

import com.eureka.ip.team1.urjung_main.chatbot.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;

public interface ChatInteractionFacade {
    ApiResponse<ChatResponseDto> chat(String id, ChatRequestDto chatRequestDto);
}
