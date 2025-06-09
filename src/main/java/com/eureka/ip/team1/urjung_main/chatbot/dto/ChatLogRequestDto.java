package com.eureka.ip.team1.urjung_main.chatbot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ChatLogRequestDto {

	private String sessionId;
	
	private String userId;
	
	private String role;
	
	private String message;
	
	// 팩토리 메소드
	public static ChatLogRequestDto createChatLogRequestDto(
			String sessionId, String userId, String role, String message
	) {
		ChatLogRequestDto chatLogRequestDto = new ChatLogRequestDto();
		
		chatLogRequestDto.sessionId = sessionId;
		chatLogRequestDto.userId = userId;
		chatLogRequestDto.role = role;
		chatLogRequestDto.message = message;
		
		return chatLogRequestDto;
	}
	
}
