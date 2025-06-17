package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatLogRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatLogResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.Content;
import com.eureka.ip.team1.urjung_main.chatbot.entity.ChatContext;
import com.eureka.ip.team1.urjung_main.chatbot.entity.UserChatAnalysis;

import java.util.List;

public interface ChatLogService {
	
	ChatLogResponseDto saveRecentAndPermanentChatLog(ChatLogRequestDto chatLogRequestDto);
	List<Content> getRecentChatHistory(String userId, String sessionId);

	void saveAnswer(String sessionId, String answer, String userId);

	UserChatAnalysis getAnalysis(String sessionId);
	void saveChatContext(String sessionId, ChatContext context);
	ChatContext getChatContext(String sessionId);
	void clearAnalysis(String sessionId);
	void saveCurrentStep(String sessionId, int step);
}
