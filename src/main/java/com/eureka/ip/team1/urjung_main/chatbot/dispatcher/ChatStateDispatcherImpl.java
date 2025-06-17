package com.eureka.ip.team1.urjung_main.chatbot.dispatcher;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import com.eureka.ip.team1.urjung_main.chatbot.handler.ChatStateHandler;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatStateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

    private Map<ChatState, ChatStateHandler> handlerMap = new HashMap<>();

    @PostConstruct
    public void init(){
        for (ChatStateHandler handler : handlers) {
            handlerMap.put(handler.getState(),handler);
        }
    }

    @Override
    public Flux<ChatResponseDto> dispatch(String userId, ChatRequestDto requestDto) {
        return chatStateService.getState(requestDto.getSessionId())
                .flatMapMany(state -> {
                    ChatStateHandler handler =
                            handlerMap.getOrDefault(state, defaultHandler);
                    return handler.handle(userId, requestDto);
                });
    }
}
