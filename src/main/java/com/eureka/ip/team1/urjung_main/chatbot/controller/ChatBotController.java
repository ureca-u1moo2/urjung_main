package com.eureka.ip.team1.urjung_main.chatbot.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.facade.ChatInteractionFacade;
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
    private final ChatInteractionFacade chatInteractionFacade;

    @PostMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChatResponseDto> chat(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ChatRequestDto requestDto) {
        return chatInteractionFacade.chat(userDetails.getUserId(), requestDto);
//        return chatInteractionFacade.chat("40dc21a3-5663-40bb-b792-795db7ed4fad", requestDto);
    }

    @PostMapping(value = "/recommend/start", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChatResponseDto> startRecommendation(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ChatRequestDto requestDto) {
        return chatInteractionFacade.startRecommendationFlow(userDetails.getUserId(),requestDto);
    }

    @PostMapping(value = "/state/default", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChatResponseDto> changeStateToDefault(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ChatRequestDto requestDto) {
        return chatInteractionFacade.changeStateToDefault(userDetails.getUserId(),requestDto);
    }

    @PostMapping(value = "/analysis/start", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChatResponseDto> changeStateToPersonalAnalysis(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ChatRequestDto requestDto) {
        return chatInteractionFacade.changeStateToPersonalAnalysis(userDetails.getUserId(),requestDto);
    }
}
