package com.eureka.ip.team1.urjung_main.chatbot.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.chatbot.dispatcher.ChatStateDispatcher;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatBotController {
    private final ChatStateDispatcher chatStateDispatcher;

    @PostMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChatResponseDto> chat(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ChatRequestDto requestDto) {
        return chatStateDispatcher.dispatch(userDetails.getUserId(), requestDto);
    }
}
