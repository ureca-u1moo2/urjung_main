package com.eureka.ip.team1.urjung_main.chatbot.dto;

import lombok.Getter;

@Getter
public class ChatLogRequestDto {

	private String sessionId;
	
	private String userId;
	
	private String role;
	
	private String message;
	
}
