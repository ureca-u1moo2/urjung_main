package com.eureka.ip.team1.urjung_main.chatbot.repository;

import java.util.List;

import com.eureka.ip.team1.urjung_main.chatbot.dto.Content;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecentChatLogRepository {

	private final StringRedisTemplate stringRedisTemplate;
	
	private final ObjectMapper objectMapper;

	// KEY -> chat::user::{userId}::session::{sessionId}
	private static final String KEY_FORMAT = "chat::user::%s::session::%s";
	
	// 최대 메시지 20개
	private static final int MAX_MESSAGES = 20;
	
	// key에 해당하는 최근 20개 대화 내역 불러오기
	public List<Content> readHistory(String userId, String sessionId) {
		String key = generateKey(userId, sessionId);
		
		List<String> messages = stringRedisTemplate.opsForList().range(key, 0, -1);
		
		return messages.stream()
				.map(this::parseContent)
				.toList();
	}
	
	// 최근 대화 내역 key에 저장
	public void saveHistory(String userId, String sessionId, Content content) {
		try {
			String key = generateKey(userId, sessionId);
			
			String json = objectMapper.writeValueAsString(content);
			
			stringRedisTemplate.opsForList().leftPush(key, json);
			
			trimToMaxMessages(userId, sessionId);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("메시지 저장에 실패하였습니다.");
		}
	}
	
	// key 존재 여부 확인
	public boolean exists(String userId, String sessionId) {
		String key = generateKey(userId, sessionId);
		
		return stringRedisTemplate.hasKey(key);
	}
	
	// 최근 대화 내역 20개 제한 (삭제)
	private void trimToMaxMessages(String userId, String sessionId) {
		String key = generateKey(userId, sessionId);
		
		Long size = stringRedisTemplate.opsForList().size(key);
		
		if(size - MAX_MESSAGES > 0) {
			 Long trimSize = size - MAX_MESSAGES;
			
			for(int i = 0; i < trimSize; i++) {
				stringRedisTemplate.opsForList().rightPop(key);
			}
		}
	}
	
	// key 생성 메소드
	private String generateKey(String userId, String sessionId) {
		return KEY_FORMAT.formatted(userId, sessionId);
	}
	
	// json 파싱
	private Content parseContent(String json) {
		try {
			return objectMapper.readValue(json, Content.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("JSON 파싱에 실패하였습니다.");
		}
	}
}
