package com.eureka.ip.team1.urjung_main.chatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import com.eureka.ip.team1.urjung_main.chatbot.document.PermanentChatLog;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatLogRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatLogResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.Content;
import com.eureka.ip.team1.urjung_main.chatbot.dto.Part;
import com.eureka.ip.team1.urjung_main.chatbot.repository.PermanentChatLogRepository;
import com.eureka.ip.team1.urjung_main.chatbot.repository.RecentChatLogRepository;
import com.eureka.ip.team1.urjung_main.common.exception.InternalServerErrorException;

@ExtendWith(MockitoExtension.class)
class ChatLogServiceImplTest {
	
	@InjectMocks
	private ChatLogServiceImpl chatLogServiceImpl;
	
	@Mock
	private PermanentChatLogRepository permanentChatLogRepository;
	
	@Mock
	private RecentChatLogRepository recentChatLogRepository;
	
	@Test
	void saveRecentAndPermanentChatLog_세션_생성_성공_Test() {
		// given
		String userId = UUID.randomUUID().toString();
		String role = "user";
		String message = "hi";
		
		ChatLogRequestDto chatLogRequestDto =
				ChatLogRequestDto.createChatLogRequestDto(null, userId, role, message);
		
		Content content = Content.createContent(
				role,
				List.of(Part.createPart(message))
		);

		// Mock 설정
	    when(permanentChatLogRepository.findBySessionId(any(String.class)))
        	.thenReturn(Optional.empty());
		
	    when(permanentChatLogRepository.save(any(PermanentChatLog.class)))
        	.thenAnswer(invocation -> invocation.getArgument(0));

		
		// when
		ChatLogResponseDto responseDto = chatLogServiceImpl.saveRecentAndPermanentChatLog(chatLogRequestDto);
		
		// then
		assertEquals(userId, responseDto.getUserId());
		assertNotNull(responseDto.getSessionId());
		assertEquals(1, responseDto.getMessages().size());
		assertEquals(content.getRole(),
				responseDto.getMessages().get(0).getRole());
		assertEquals(content.getParts().get(0).getText(),
				responseDto.getMessages().get(0).getParts().get(0).getText());
		
		verify(recentChatLogRepository)
			.saveHistory(eq(userId), any(String.class), eq(content));
		verify(permanentChatLogRepository)
			.findBySessionId(any(String.class));
		verify(permanentChatLogRepository, times(2))
			.save(any(PermanentChatLog.class));
		
	}
	
	@Test
	void saveRecentAndPermanentChatLog_기존_세션_존재_성공_Test() {
		// given
		String userId = UUID.randomUUID().toString();
		String sessionId = UUID.randomUUID().toString();
		String userRole = "user";
		String userMessage = "hi";
		String botRole = "bot";
		String botMessage = "bye";
		

	    Content existingContent = Content.createContent(
	        botRole, 
	        new ArrayList<>(List.of(Part.createPart(botMessage))) 
        );
	    
	    List<Content> existingMessages = new ArrayList<>();
	    
	    existingMessages.add(existingContent);
	    
	    PermanentChatLog existingChatLog = PermanentChatLog.createChatLog(
	        userId,
	        sessionId,
	        existingMessages 
	    );
	    
		Content content = Content.createContent(
				userRole,
				List.of(Part.createPart(userMessage))
		);

		ChatLogRequestDto chatLogRequestDto =
				ChatLogRequestDto.createChatLogRequestDto(sessionId, userId, userRole, userMessage);

		// Mock 설정
	    when(permanentChatLogRepository.findBySessionId(eq(sessionId)))
    		.thenReturn(Optional.of(existingChatLog));
	    
	    when(permanentChatLogRepository.save(any(PermanentChatLog.class)))
        	.thenAnswer(invocation -> invocation.getArgument(0));

		
		// when
		ChatLogResponseDto responseDto = chatLogServiceImpl.saveRecentAndPermanentChatLog(chatLogRequestDto);
		
		// then
		assertEquals(userId, responseDto.getUserId());
		assertEquals(sessionId, responseDto.getSessionId());
		assertEquals(2, responseDto.getMessages().size());
		// 첫 번째 메시지
		assertEquals(existingContent.getRole(),
				responseDto.getMessages().get(0).getRole());
		assertEquals(existingContent.getParts().get(0).getText(),
				responseDto.getMessages().get(0).getParts().get(0).getText());
		
		// 두 번째 메시지
		assertEquals(content.getRole(),
				responseDto.getMessages().get(1).getRole());
		assertEquals(content.getParts().get(0).getText(),
				responseDto.getMessages().get(1).getParts().get(0).getText());
		
		
		verify(recentChatLogRepository)
			.saveHistory(eq(userId), any(String.class), eq(content));
		verify(permanentChatLogRepository)
			.findBySessionId(any(String.class));
		verify(permanentChatLogRepository, times(1))
			.save(any(PermanentChatLog.class));
		
	}
	
	@Test
	void saveRecentAndPermanentChatLog_DuplicateKeyException_3번_InternalServerErrorException_Test() {
	    // given
	    String userId = UUID.randomUUID().toString();
	    String role = "user";
	    String message = "hi";
	    
	    ChatLogRequestDto chatLogRequestDto = 
	        ChatLogRequestDto.createChatLogRequestDto(null, userId, role, message);
	    
	    when(permanentChatLogRepository.findBySessionId(any(String.class)))
	        .thenReturn(Optional.empty());
	    
	    when(permanentChatLogRepository.save(any(PermanentChatLog.class)))
	        .thenThrow(new DuplicateKeyException("Duplicate key"));
	    
	    // when & then
	    assertThrows(InternalServerErrorException.class, () -> {
	        chatLogServiceImpl.saveRecentAndPermanentChatLog(chatLogRequestDto);
	    });
	    
	    verify(permanentChatLogRepository, times(3)).findBySessionId(any(String.class));
	    verify(permanentChatLogRepository, times(3)).save(any(PermanentChatLog.class));
	}
}
