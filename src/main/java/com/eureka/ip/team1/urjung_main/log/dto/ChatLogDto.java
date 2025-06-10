package com.eureka.ip.team1.urjung_main.log.dto;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatLogDto {
    private String userId;
    private String sessionId;
    private Instant timestamp;
    private String userMessage;
    private Topic intent;
    private String botResponse;
    private List<String> recommendedItems;
    private String selectedItem;
    private Long latencyMs;
}
