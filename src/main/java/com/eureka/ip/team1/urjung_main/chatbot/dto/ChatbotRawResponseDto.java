package com.eureka.ip.team1.urjung_main.chatbot.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatbotRawResponseDto {
    private String reply;                         // 사용자에게 보여줄 텍스트
    private List<String> planIds;      // 추천 요금제 ID 목록
}
