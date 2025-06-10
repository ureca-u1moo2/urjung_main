package com.eureka.ip.team1.urjung_main.chatbot.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
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
class PermanentChatLogRepositoryTest {

	@Autowired
	private PermanentChatLogRepository permanentChatLogRepository;
	
    private List<String> createdTestIds = new ArrayList<>();

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
		
        List<PermanentChatLog> testLogs = List.of(
                PermanentChatLog.createChatLog(userId, sessionId, messages),
                PermanentChatLog.createChatLog(userId, sessionId1, messages),
                PermanentChatLog.createChatLog(userId, sessionId2, messages),
                PermanentChatLog.createChatLog(userId, sessionId3, messages)
        );
        
        createdTestIds = permanentChatLogRepository.saveAll(testLogs)
                .stream()
                .map(PermanentChatLog::getId)
                .collect(Collectors.toList());
	}
	
	@AfterEach
	void cleanUp() {
		permanentChatLogRepository.deleteAllById(createdTestIds);
		createdTestIds.clear();
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
