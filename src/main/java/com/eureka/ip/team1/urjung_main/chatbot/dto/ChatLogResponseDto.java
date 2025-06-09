package com.eureka.ip.team1.urjung_main.chatbot.dto;

import java.util.List;

import com.eureka.ip.team1.urjung_main.chatbot.document.PermanentChatLog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ChatLogResponseDto {

	private String sessionId;
	
	private String userId;
	
	private List<Content> messages;
	
	public static ChatLogResponseDto fromChatLog(PermanentChatLog chatLog) {
		ChatLogResponseDto response = new ChatLogResponseDto();
		
		response.sessionId = chatLog.getSessionId();
		response.userId = chatLog.getUserId();
		response.messages = chatLog.getMessages();
		
		return response;
	}
}
