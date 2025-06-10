package com.eureka.ip.team1.urjung_main.chatbot.facade;

import com.eureka.ip.team1.urjung_main.chatbot.dto.Button;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ButtonType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.generator.PromptStrategyFactory;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.*;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatBotService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordService;
import com.eureka.ip.team1.urjung_main.chatbot.utils.JsonUtil;
import com.eureka.ip.team1.urjung_main.log.dto.ChatLogDto;
import com.eureka.ip.team1.urjung_main.log.service.ElasticsearchLogService;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatInteractionFacadeImpl implements ChatInteractionFacade {
    private final ChatBotService chatBotService;
    private final PromptStrategyFactory promptStrategyFactory;

    private final ForbiddenWordService forbiddenWordService;
    private final ElasticsearchLogService elasticsearchLogService;
    private final PlanService planService;

    @Override
    public Flux<ChatResponseDto> chat(String userId, ChatRequestDto requestDto) {
        // 금칙어 필터링 우선 수행
        if (forbiddenWordService.containsForbiddenWord(requestDto.getMessage())) {
            ChatResponseDto responseDto = ChatResponseDto.builder()
                    .message("입력할 수 없는 단어가 포함되어 있습니다.")
                    .build();
            return Flux.just(responseDto);
        }
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

        return chatBotService.handleUserMessage(prompt, requestDto.getMessage())
                .map(response -> attachButtonsIfNeeded(response, topic))
                .flatMap(response -> {
                    try {
                        return saveChatLog(userId, requestDto, response, topic)
                                .thenReturn(response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private ChatResponseDto attachButtonsIfNeeded(ChatResponseDto response, Topic topic) {
        if (topic == Topic.ALL_PLAN_INFORMATION) {
            List<Button> buttons = List.of(
                    Button.builder()
                            .label("전체 요금 보러가기")
                            .type(ButtonType.URL)
                            .value("https://naver.com")
                            .build()
            );
            response.setButtons(buttons);
        }
        return response;
    }


    private String generatePromptByTopic(ChatRequestDto dto, Topic topic) {
        PromptStrategy strategy = promptStrategyFactory.getStrategy(topic);
        return switch (topic) {
            case RECOMMENDATION_PLAN -> "사용자의 요금제 이용 패턴에 맞는 요금제를 추천해줘.";
            case PLAN_DETAIL -> {
                List<PlanDto> plans = planService.getPlansSorted("popular");
                String plansJson = JsonUtil.toJson(plans);
                if (strategy instanceof PlanDetailPromptStrategy) {
                    PlanDetailPromptStrategy planDetailStrategy = (PlanDetailPromptStrategy) strategy;
                    yield planDetailStrategy.generatePrompt(plansJson);
                }
                throw new ClassCastException();
            }
            case INFO -> {
                if (strategy instanceof ServiceInfoPromptStrategy) {
                    ServiceInfoPromptStrategy infoStrategy = (ServiceInfoPromptStrategy) strategy;
                    yield infoStrategy.generatePrompt();
                }
                throw new ClassCastException();
            }

            case ALL_PLAN_INFORMATION -> {
                if(strategy instanceof AllPlanPromptStrategy){
                    AllPlanPromptStrategy allPlanStrategy = (AllPlanPromptStrategy) strategy;
                    yield allPlanStrategy.generatePrompt();
                }
                throw new ClassCastException();
            }
            default -> {
                if (strategy instanceof EtcPromptStrategy) {
                    EtcPromptStrategy etcStrategy = (EtcPromptStrategy) strategy;
                    yield etcStrategy.generatePrompt();
                }
                throw new ClassCastException();
            }
        };
    }

    private Mono<Void> saveChatLog(String userId, ChatRequestDto requestDto, ChatResponseDto response, Topic topic) throws IOException {
        return Mono.fromRunnable(() -> {
            try {
                ChatLogDto chatLogDto = new ChatLogDto(
                        userId,
                        requestDto.getSessionId(),
                        Instant.now(),
                        requestDto.getMessage(),
                        topic,
                        response.getMessage(),
                        null,
                        null,
                        null
                );
                elasticsearchLogService.saveChatLog(chatLogDto); // 동기 호출
            } catch (Exception e) {
                throw new RuntimeException("Chat log 저장 중 오류 발생", e);
            }
        });
    }

}
