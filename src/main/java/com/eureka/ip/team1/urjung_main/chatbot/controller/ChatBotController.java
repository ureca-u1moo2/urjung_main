package com.eureka.ip.team1.urjung_main.chatbot.controller;

import com.eureka.ip.team1.urjung_main.chatbot.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.facade.ChatInteractionFacade;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatBotController{
    private final ChatInteractionFacade chatInteractionFacade;
    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponseDto>> chat(@RequestBody ChatRequestDto requestDto){
        ApiResponse<ChatResponseDto> response = chatInteractionFacade.chat("1", requestDto);
        return ResponseEntity.ok().body(response);
    }
}
