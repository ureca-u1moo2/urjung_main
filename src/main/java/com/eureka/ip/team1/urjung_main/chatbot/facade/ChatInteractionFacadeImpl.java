package com.eureka.ip.team1.urjung_main.chatbot.facade;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.generator.PromptStrategyFactory;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.PromptStrategy;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.ServiceInfoPromptStrategy;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.TopicClassifyPromptStrategy;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatInteractionFacadeImpl implements ChatInteractionFacade {
    private final ChatBotService chatBotService;
    private final PromptStrategyFactory promptStrategyFactory;

    @Override
    public Flux<ChatResponseDto> chat(String userId, ChatRequestDto requestDto) {
        // 1    : 상태 확인 → 성향 분석 중이면 별도 처리
//        if (isInPersonalityAnalysisState(userId)) {
//            return handlePersonalityAnalysis(userId, requestDto);
//        }

        // 2 : 토픽 분류 → 응답 흐름 위임
        TopicClassifyPromptStrategy topicClassifyPromptStrategy = new TopicClassifyPromptStrategy();
        String classifyPrompt = topicClassifyPromptStrategy.generatePrompt();
        return chatBotService.classifyTopic(classifyPrompt, requestDto.getMessage())
                .flatMapMany(response -> {
                    Topic topic = response.getTopic();
                    String waitMessage = response.getWaitMessage();
                    Mono<ChatResponseDto> waitingResponse = Mono.just(ChatResponseDto.builder()
                            .message(waitMessage)
                            .build());

                    return Flux.concat(waitingResponse, handleByTopic(userId, requestDto, topic));
                });
    }

    private boolean isInPersonalityAnalysisState(String userId) {
        // 나중에 구현
        return false;
    }

//    private Flux<ChatResponseDto> handlePersonalityAnalysis(String userId, ChatRequestDto dto) {
//        // 향후 성향 분석 질문 분기 로직
//        return Flux.just(ChatResponseDto.of("성향 분석 중입니다. 다음 질문에 답해주세요."));
//    }

    private Mono<ChatResponseDto> handleByTopic(String userId, ChatRequestDto requestDto, Topic topic) {
        String prompt = generatePromptByTopic(requestDto, topic);
        Mono<ChatResponseDto> rawResponse = chatBotService.handleUserMessage(prompt, requestDto.getMessage());

        return attachLoggingAndBuffer(rawResponse);
    }

    private String generatePromptByTopic(ChatRequestDto dto, Topic topic) {
        PromptStrategy strategy = promptStrategyFactory.getStrategy(topic);
        return switch (topic) {
            case RECOMMENDATION_PLAN -> "사용자의 요금제 이용 패턴에 맞는 요금제를 추천해줘.";
            case PLAN_DETAIL -> "해당 요금제에 대해 자세히 알려줘. 특히 혜택, 가격, 데이터 정보 위주로.";
            case INFO -> {
                if (strategy instanceof ServiceInfoPromptStrategy) {
                    ServiceInfoPromptStrategy infoStrategy = (ServiceInfoPromptStrategy) strategy;
                    yield infoStrategy.generatePrompt();
                }
                throw new ClassCastException();
            }

            case ALL_PLAN_INFORMATION -> "전체 요금제를 알려줘. URL이나 목록 형태로 정리해서.";
            default -> dto.getMessage();
        };
    }

    private Mono<ChatResponseDto> attachLoggingAndBuffer(Mono<ChatResponseDto> mono) {
        return mono
                .doOnNext(msg -> log.info("[챗봇 출력] {}", msg.getMessage()))
                .flatMap(msg ->
                        // 저장로직 추가
                        Mono.just(msg)
                );
    }
}
