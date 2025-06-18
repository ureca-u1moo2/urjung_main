package com.eureka.ip.team1.urjung_main.chatbot.facade;

import com.eureka.ip.team1.urjung_main.chatbot.dispatcher.ChatStateDispatcher;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import com.eureka.ip.team1.urjung_main.chatbot.handler.RecommendationStartHandler;
import com.eureka.ip.team1.urjung_main.chatbot.processor.ChatLogProcessor;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatLogService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatStateService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatInteractionFacadeImpl implements ChatInteractionFacade {
    private final ChatStateDispatcher dispatcher;
    private final ChatLogProcessor chatLogProcessor;
    private final ForbiddenWordService forbiddenWordService;
    private final ChatStateService chatStateService;
    private final ChatLogService chatLogService;

    @Override
    public Flux<ChatResponseDto> chat(String userId, ChatRequestDto requestDto) {
        // 금칙어 필터링 우선 수행
        if (forbiddenWordService.containsForbiddenWord(requestDto.getMessage())) {
            ChatResponseDto responseDto = ChatResponseDto.builder()
                    .message("입력할 수 없는 단어가 포함되어 있습니다.")
                    .build();
            return Flux.just(responseDto);
        }
        long start = System.currentTimeMillis();

        return Mono.defer(() -> chatLogProcessor.saveMongoLog(userId, requestDto, "user", null))
                .thenMany(
                        dispatcher.dispatch(userId, requestDto)
                                .flatMap(response -> {
                                    if (response.getType() == null) {
                                        return Mono.when(
                                                chatLogProcessor.saveMongoLog(userId, requestDto, "model", response),
                                                chatLogProcessor.saveEmbeddingIfNeeded(requestDto.getMessage()),
                                                chatLogProcessor.saveElasticsearchLog(userId, requestDto, response, response.getTopic(), System.currentTimeMillis() - start)
                                        ).thenReturn(response); // response 그대로 다시 방출
                                    }
                                    return Mono.just(response); // 다른 응답은 그대로 통과
                                })
                );
    }

    @Override
    public Flux<ChatResponseDto> startRecommendationFlow(String userId, ChatRequestDto requestDto) {
        return chatStateService.setState(requestDto.getSessionId(),ChatState.RECOMMENDATION_START)
                .thenMany(dispatcher.dispatch(userId,requestDto));
    }

    @Override
    public Flux<ChatResponseDto> changeStateToDefault(String userId, ChatRequestDto requestDto) {
        chatLogService.clearAnalysis(requestDto.getSessionId());
        return chatStateService.setState(requestDto.getSessionId(),ChatState.IDLE)
                .thenReturn(ChatResponseDto.builder()
                        .message("요금제 추천 모드가 종료되었습니다").build()).flux();
    }

    @Override
    public Flux<ChatResponseDto> changeStateToPersonalAnalysis(String userId, ChatRequestDto requestDto) {
        return chatStateService.setState(requestDto.getSessionId(),ChatState.AWAITING_PERSONAL_ANALYSIS_START)
                .thenMany(dispatcher.dispatch(userId,requestDto));
    }
}
