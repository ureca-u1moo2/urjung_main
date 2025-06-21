package com.eureka.ip.team1.urjung_main.chatbot.dispatcher;

import com.eureka.ip.team1.urjung_main.chatbot.component.Button;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatCommand;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import com.eureka.ip.team1.urjung_main.chatbot.handler.ChatStateHandler;
import com.eureka.ip.team1.urjung_main.chatbot.processor.ChatLogProcessor;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatStateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatStateDispatcherImpl implements ChatStateDispatcher {

    private final ChatStateService chatStateService;
    private final List<ChatStateHandler> handlers;
    private final ChatStateHandler defaultHandler;
    private final ChatLogProcessor chatLogProcessor;

    private Map<ChatState, ChatStateHandler> handlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (ChatStateHandler handler : handlers) {
            handlerMap.put(handler.getState(), handler);
        }
    }

    @Override
    public Flux<ChatResponseDto> dispatch(String userId, ChatRequestDto requestDto) {
        ChatCommand command = requestDto.getCommand();
        String sessionId = requestDto.getSessionId();
        if (command.equals(ChatCommand.START_ANALYSIS)) {
            return chatStateService.setState(sessionId, ChatState.AWAITING_PERSONAL_ANALYSIS_START)
                    .thenMany(dispatchByCurrentState(userId, requestDto));
        }

        if (command.equals(ChatCommand.CANCEL)) {
            return chatStateService.setState(sessionId, ChatState.IDLE).thenReturn(
                    ChatResponseDto.ofInfoReply("요금제 추천 모드가 종료되었습니다", List.of(Button.planPage(), Button.recommendStart()))).flux();
        }

        if (command.equals(ChatCommand.START_RECOMMENDATION)) {
            return chatStateService.setState(sessionId, ChatState.RECOMMENDATION_START)
                    .thenMany(dispatchByCurrentState(userId, requestDto));
        }

        return dispatchByCurrentState(userId, requestDto);
    }

    private Flux<ChatResponseDto> dispatchByCurrentState(String userId, ChatRequestDto requestDto) {
        long start = System.currentTimeMillis();

        return chatStateService.getState(requestDto.getSessionId())
                .flatMapMany(state -> {
                    ChatStateHandler handler = handlerMap.getOrDefault(state, defaultHandler);
                    return handler.handle(userId, requestDto);
                })
                .flatMap(response -> {
                    if (requestDto.getCommand() == ChatCommand.CHAT && response.getType() != ChatResponseType.WAITING) {
                        return Mono.when(
                                chatLogProcessor.saveRecentLog(userId, requestDto, response),
                                chatLogProcessor.savePermanentLog(userId, requestDto, response),
                                chatLogProcessor.saveEmbeddingIfNeeded(requestDto.getMessage()),
                                chatLogProcessor.saveElasticsearchLog(userId, requestDto, response, response.getTopic(), System.currentTimeMillis() - start)
                        ).thenReturn(response);
                    }
                    return Mono.just(response);
                });
    }
}
