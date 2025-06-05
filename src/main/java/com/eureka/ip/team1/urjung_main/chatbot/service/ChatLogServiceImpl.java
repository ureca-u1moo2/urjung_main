package com.eureka.ip.team1.urjung_main.chatbot.service;

import java.util.UUID;

public class ChatLogServiceImpl implements ChatLogService {

	@Override
	public String getOrCreateDefaultUUID(String sessionId) {
		if(sessionId == null || sessionId.isEmpty()) {
			return UUID.randomUUID().toString();
		}
		
		return sessionId;
	}

}
