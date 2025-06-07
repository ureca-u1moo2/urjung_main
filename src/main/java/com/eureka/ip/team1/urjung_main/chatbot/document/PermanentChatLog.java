package com.eureka.ip.team1.urjung_main.chatbot.document;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.eureka.ip.team1.urjung_main.chatbot.dto.Content;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Document(collection = "chat_log")
@Getter
@ToString
@NoArgsConstructor
public class PermanentChatLog {
	
	@Id
	private String id;
	
	@Indexed
	private String userId;
	
	@Indexed(unique = true)
	private String sessionId;
	
	private List<Content> messages;
	
	// 팩토리 메소드
	public static PermanentChatLog createChatLog(
			String userId, String sessionId, List<Content> messages
	) {
		PermanentChatLog chatLog = new PermanentChatLog();
		
		chatLog.userId = userId;
		chatLog.sessionId = sessionId;
		chatLog.messages = messages;
		
		return chatLog;
	}
}
