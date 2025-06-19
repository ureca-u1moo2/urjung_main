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
	public static ChatLogRequestDto createChatModelLogRequestDto(
			String sessionId, String userId, String message
	) {
		ChatLogRequestDto chatLogRequestDto = new ChatLogRequestDto();
		
		chatLogRequestDto.sessionId = sessionId;
		chatLogRequestDto.userId = userId;
		chatLogRequestDto.role = "model";
		chatLogRequestDto.message = message;
		
		return chatLogRequestDto;
	}

	// 팩토리 메소드
	public static ChatLogRequestDto createChatUserLogRequestDto(
			String sessionId, String userId, String message
	) {
		ChatLogRequestDto chatLogRequestDto = new ChatLogRequestDto();

		chatLogRequestDto.sessionId = sessionId;
		chatLogRequestDto.userId = userId;
		chatLogRequestDto.role = "user";
		chatLogRequestDto.message = message;

		return chatLogRequestDto;
	}
	
}
