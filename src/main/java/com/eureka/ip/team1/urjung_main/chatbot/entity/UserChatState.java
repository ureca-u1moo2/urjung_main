package com.eureka.ip.team1.urjung_main.chatbot.entity;

import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("userChatState")
public class UserChatState {
    @Id
    private String sessionId;
    private ChatState state;
}