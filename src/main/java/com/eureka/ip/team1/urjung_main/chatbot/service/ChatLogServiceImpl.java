package com.eureka.ip.team1.urjung_main.chatbot.service;

import java.util.*;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.eureka.ip.team1.urjung_main.chatbot.document.PermanentChatLog;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatLogRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatLogResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.Content;
import com.eureka.ip.team1.urjung_main.chatbot.dto.Part;
import com.eureka.ip.team1.urjung_main.chatbot.repository.PermanentChatLogRepository;
import com.eureka.ip.team1.urjung_main.chatbot.repository.RecentChatLogRepository;
import com.eureka.ip.team1.urjung_main.common.exception.InternalServerErrorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatLogServiceImpl implements ChatLogService {

    private final RecentChatLogRepository recentChatLogRepository;
    private final PermanentChatLogRepository permanentChatLogRepository;

    @Override
    public ChatLogResponseDto saveRecentAndPermanentChatLog(ChatLogRequestDto chatLogRequestDto) {
        Content content = Content.createContent(
                chatLogRequestDto.getRole(),
                List.of(Part.createPart(chatLogRequestDto.getMessage()))
        );

        String sessionId = getOrCreateDefaultUUID(chatLogRequestDto.getSessionId());
        String userId = chatLogRequestDto.getUserId();

        recentChatLogRepository.saveHistory(userId, sessionId, content);

        PermanentChatLog chatLog = getOrCreateSession(userId, sessionId);

        chatLog.getMessages().add(content);

        return ChatLogResponseDto.fromChatLog(
                permanentChatLogRepository.save(chatLog)
        );
    }

    public List<Content> getRecentChatHistory(String userId, String sessionId) {
        List<Content> contents = new ArrayList<>(recentChatLogRepository.readHistory(userId, sessionId));
        Collections.reverse(contents);

        return contents;
    }

    private PermanentChatLog getOrCreateSession(String userId, String sessionId) {
        for (int i = 0; i < 3; i++) {
            try {
                Optional<PermanentChatLog> existing = permanentChatLogRepository.findBySessionId(sessionId);

                if (existing.isPresent()) return existing.get();

                PermanentChatLog chatLog = PermanentChatLog.createChatLog(
                        userId,
                        sessionId,
                        new ArrayList<>()
                );

                return permanentChatLogRepository.save(chatLog);
            } catch (DuplicateKeyException e) {
                if (i == 2) throw new InternalServerErrorException();
            }
        }

        throw new InternalServerErrorException();

    }


    private String getOrCreateDefaultUUID(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return UUID.randomUUID().toString();
        }

        return sessionId;
    }

}
