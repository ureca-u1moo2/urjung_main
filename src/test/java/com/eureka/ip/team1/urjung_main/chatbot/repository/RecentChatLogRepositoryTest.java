package com.eureka.ip.team1.urjung_main.chatbot.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.eureka.ip.team1.urjung_main.chatbot.dto.Content;
import com.eureka.ip.team1.urjung_main.chatbot.dto.Part;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class RecentChatLogRepositoryTest {

	@Autowired
	private RecentChatLogRepository recentChatLogRepository;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private static final String KEY_FORMAT = "chat::user::%s::session::%s";
	
	private String userId = UUID.randomUUID().toString();
	
	private String sessionId = UUID.randomUUID().toString();
	
	private static final int MAX_MESSAGES = 20;

	@BeforeAll
	void beforeAll() {
		String key = generateKey(
				userId,
				sessionId
		);
		
		List<Content> listContent = new ArrayList<>();
		
		for(int i = 0; i < MAX_MESSAGES / 2; i++) {
			Content userContent = Content.createContent(
					"user",
					List.of(Part.createPart("hi%d".formatted(i)))
			);
			
			Content botContent = Content.createContent(
					"bot",
					List.of(Part.createPart("bye%d".formatted(i)))
			);
			
			listContent.add(userContent);
			listContent.add(botContent);
		}
		
		List<String> jsonList = listContent.stream()
			.map(content -> {
				try {
					return objectMapper.writeValueAsString(content);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					throw new RuntimeException("JSON 변환 실패");				}
			})
			.toList();
		
		stringRedisTemplate.opsForList().leftPushAll(key, jsonList);
	}
	
	@AfterAll
	void cleanUp() {
		stringRedisTemplate.delete(generateKey(userId, sessionId));
	}
	
	@Test
	public void trimToMaxMessages_Test() {
	    String newUserId = UUID.randomUUID().toString();
	    String newSessionId = UUID.randomUUID().toString();
	    String key = generateKey(newUserId, newSessionId);
	    
	    for(int i = 0; i < 25; i++) {
	        Content content = Content.createContent(
	            "user",
	            List.of(Part.createPart("message" + i))
	        );
	        recentChatLogRepository.saveHistory(newUserId, newSessionId, content);
	        
	        Long currentSize = stringRedisTemplate.opsForList().size(key);
	    }
	    
	    Long finalSize = stringRedisTemplate.opsForList().size(key);
	    assertEquals(MAX_MESSAGES, finalSize);
	    
	    stringRedisTemplate.delete(key);
	}
	
	@Test
	void exists_존재X_Test() {
		assertFalse(recentChatLogRepository.exists(null, null));
	}
	
	@Test
	void exists_존재O_Test() {
		assertTrue(recentChatLogRepository.exists(userId, sessionId));
	}
	
	@Test
	public void saveHistory_성공_사이즈_조절_Test() {
		String key = generateKey(userId, sessionId);
		
		Content userContent = Content.createContent(
				"user",
				List.of(Part.createPart("hi"))
		);
		
		recentChatLogRepository.saveHistory(userId, sessionId, userContent);
		
		assertEquals(MAX_MESSAGES, stringRedisTemplate.opsForList().size(key));
		
		assertEquals(
				userContent.getRole(),
				parseContent(stringRedisTemplate.opsForList().getFirst(key))
					.getRole()
		);
		
		assertEquals(
				userContent.getParts().get(0).getText(),
				parseContent(stringRedisTemplate.opsForList().getFirst(key))
					.getParts()
					.get(0)
					.getText()
		);
	}
	
	@Test
	void saveHistory_JSON_파싱_실패_Test() {
	    Content invalidContent = Content.createContent(
	            "user",
	            List.of(Part.createPart("test message"))
	    );
	    
	    ObjectMapper mockObjectMapper = Mockito.mock(ObjectMapper.class);
	    ReflectionTestUtils.setField(recentChatLogRepository, "objectMapper", mockObjectMapper);
	    
	    try {
	        Mockito.when(mockObjectMapper.writeValueAsString(Mockito.any()))
	               .thenThrow(new JsonProcessingException("JSON 처리 오류") {});
	        
	        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	            recentChatLogRepository.saveHistory(userId, sessionId, invalidContent);
	        });
	        
	        assertEquals("메시지 저장에 실패하였습니다.", exception.getMessage());
	        
	    } catch (JsonProcessingException e) {
	        fail("Mock 설정 실패");
	    } finally {
	        ReflectionTestUtils.setField(recentChatLogRepository, "objectMapper", objectMapper);
	    }
	}
	
	@Test
	void readHistory_성공_Test() {
		List<Content> contentList = recentChatLogRepository.readHistory(userId, sessionId);
		
		assertEquals(20, contentList.size());
	}
	
	@Test
	void readHistory_JSON_파싱_실패_Test() {
	    String key = generateKey(userId, sessionId);
	    
	    String invalidJson1 = "{\"role\":\"user\",\"parts\":[{\"text\":\"hello\"}"; // 닫는 괄호 누락
	    String invalidJson2 = "not_a_json_string"; // 완전히 잘못된 JSON
	    String invalidJson3 = "{\"role\":\"user\",\"invalidField\":\"value\"}"; // 필수 필드 누락
	    
	    stringRedisTemplate.opsForList().leftPush(key, invalidJson1);
	    stringRedisTemplate.opsForList().leftPush(key, invalidJson2);
	    stringRedisTemplate.opsForList().leftPush(key, invalidJson3);
	    
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        recentChatLogRepository.readHistory(userId, sessionId);
	    });
	    
	    assertEquals("JSON 파싱에 실패하였습니다.", exception.getMessage());
	    
	}

	
	
	private String generateKey(String userId, String sessionId) {
		return KEY_FORMAT.formatted(userId, sessionId);
	}
	
	private Content parseContent(String json) {
		try {
			return objectMapper.readValue(json, Content.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("JSON 파싱에 실패하였습니다.");
		}
	}
}
