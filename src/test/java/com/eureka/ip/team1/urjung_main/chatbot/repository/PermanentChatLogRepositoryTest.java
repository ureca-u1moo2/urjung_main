package com.eureka.ip.team1.urjung_main.chatbot.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.eureka.ip.team1.urjung_main.chatbot.document.PermanentChatLog;
import com.eureka.ip.team1.urjung_main.chatbot.dto.Content;
import com.eureka.ip.team1.urjung_main.chatbot.dto.Part;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional
class PermanentChatLogRepositoryTest {

	@Autowired
	private PermanentChatLogRepository permanentChatLogRepository;
	
	private String userId = UUID.randomUUID().toString();
	private String sessionId = UUID.randomUUID().toString();
	private List<Content> messages = List.of(
			Content.createContent(
					"user",
					List.of(Part.createPart("hi"))
			)
	);
	
	@BeforeEach
	void setUp() {
		String sessionId1 = UUID.randomUUID().toString();
		String sessionId2 = UUID.randomUUID().toString();
		String sessionId3 = UUID.randomUUID().toString();
		
		PermanentChatLog chatLog = PermanentChatLog.createChatLog(userId, sessionId, messages);
		PermanentChatLog chatLog1 = PermanentChatLog.createChatLog(userId, sessionId1, messages);
		PermanentChatLog chatLog2 = PermanentChatLog.createChatLog(userId, sessionId2, messages);
		PermanentChatLog chatLog3 = PermanentChatLog.createChatLog(userId, sessionId3, messages);
		
		permanentChatLogRepository.save(chatLog);
		permanentChatLogRepository.save(chatLog1);
		permanentChatLogRepository.save(chatLog2);
		permanentChatLogRepository.save(chatLog3);
	}
	
	@Test
	void findAllByUserId_Test() {
		List<PermanentChatLog> chatLogList = 
				permanentChatLogRepository.findAllByUserId(userId);
		
		assertEquals(4, chatLogList.size());
		
		for(PermanentChatLog chatLog : chatLogList) {
			log.info("chatLog.sessionId : {}", chatLog.getSessionId());
		}
	}
	
	@Test
	void findBySessionId_Test() {
		Optional<PermanentChatLog> chatLog = permanentChatLogRepository.findBySessionId(sessionId);

		assertEquals(sessionId, chatLog.get().getSessionId());
		assertEquals(userId, chatLog.get().getUserId());
		assertIterableEquals(messages, chatLog.get().getMessages());
	}
}
