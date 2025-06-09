package com.eureka.ip.team1.urjung_main.chatbot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.eureka.ip.team1.urjung_main.chatbot.document.PermanentChatLog;

public interface PermanentChatLogRepository extends MongoRepository<PermanentChatLog, String>{

	List<PermanentChatLog> findAllByUserId(String userId);
	
	Optional<PermanentChatLog> findBySessionId(String sessionId);
	
}
