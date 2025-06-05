package com.eureka.ip.team1.urjung_main.chatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatLogServiceImplTest {
	
	private ChatLogServiceImpl chatLogServiceImpl;
	
	@BeforeEach
	void setUp() {
		chatLogServiceImpl = new ChatLogServiceImpl();
	}
	
	@Test
	void getOrCreateDefaultUUID_생성_test() {
		String sessionId = chatLogServiceImpl.getOrCreateDefaultUUID(null);
		
		assertNotNull(sessionId);
	}
	
	@Test
	void getOrCreateDefaultUUID_존재_test() {
		String sessionId = UUID.randomUUID().toString();
		
		
		
		assertEquals(sessionId,chatLogServiceImpl.getOrCreateDefaultUUID(sessionId));
	}
}
