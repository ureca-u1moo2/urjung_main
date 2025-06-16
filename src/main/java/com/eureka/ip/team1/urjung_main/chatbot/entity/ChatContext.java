package com.eureka.ip.team1.urjung_main.chatbot.entity;

import com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("chatContext")  // Redis 키 prefix: chatContext:{sessionId}
public class ChatContext implements Serializable {

    @Id
    private String sessionId;  // Redis에 저장할 Key

    private String phoneNumber;
    private String planId;
    private List<UsageResponseDto> usages;
    private String userNeedMessage;

    // 필요한 필드는 이곳에 자유롭게 확장 가능
}
