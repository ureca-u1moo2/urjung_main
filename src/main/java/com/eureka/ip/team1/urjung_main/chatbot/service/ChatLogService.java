package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatLogRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatLogResponseDto;

public interface ChatLogService {
	
	ChatLogResponseDto saveRecentAndPermanentChatLog(ChatLogRequestDto chatLogRequestDto);
	
}
