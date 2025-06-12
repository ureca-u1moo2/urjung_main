package com.eureka.ip.team1.urjung_main.chatbot.facade;

import com.eureka.ip.team1.urjung_main.chatbot.dto.*;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ButtonType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.generator.PromptStrategyFactory;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.PromptStrategy;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatBotService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatLogService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordService;
import com.eureka.ip.team1.urjung_main.chatbot.utils.JsonUtil;
import com.eureka.ip.team1.urjung_main.embedding.service.EmbeddingService;
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
import java.util.List;

import static com.eureka.ip.team1.urjung_main.chatbot.utils.PromptStrategyInvoker.invokeNoArgsStrategy;
import static com.eureka.ip.team1.urjung_main.chatbot.utils.PromptStrategyInvoker.invokeSingleArgStrategy;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatInteractionFacadeImpl implements ChatInteractionFacade {
    private final ChatBotService chatBotService;
    private final PromptStrategyFactory promptStrategyFactory;

    private final ForbiddenWordService forbiddenWordService;
    private final ElasticsearchLogService elasticsearchLogService;
    private final PlanService planService;
    private final EmbeddingService embeddingService;
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
        // 1 : 상태 확인 → 성향 분석 중이면 별도 처리
//        if (isInPersonalityAnalysisState(userId)) {
//            return handlePersonalityAnalysis(userId, requestDto);
//        }
        // 2 : 토픽 분류 → 응답 흐름 위임

        List<Content> recentChatHistory = chatLogService.getRecentChatHistory(userId, requestDto.getSessionId());
        String recentChayHistoryJson = JsonUtil.toJson(recentChatHistory);
        return chatBotService.classifyTopic(requestDto.getMessage(), recentChayHistoryJson)
                .flatMapMany(response -> {
                    chatLogService.saveRecentAndPermanentChatLog(ChatLogRequestDto.createChatLogRequestDto(requestDto.getSessionId(), userId, "user", requestDto.getMessage()));
                    Topic topic = response.getTopic();
                    String waitMessage = response.getWaitMessage();
                    Mono<ChatResponseDto> waitingResponse = Mono.just(ChatResponseDto.builder()
                            .type(ChatResponseType.WAITING)
                            .message(waitMessage)
                            .build());

                    return Flux.concat(waitingResponse, handleByTopic(userId, requestDto, recentChayHistoryJson, topic));
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

    private Mono<ChatResponseDto> handleByTopic(String userId, ChatRequestDto requestDto, String recentChatHistory, Topic topic) {
        String prompt = generatePromptByTopic(topic);

        long startTime = System.currentTimeMillis();

        return chatBotService.handleUserMessage(prompt, requestDto.getMessage(), recentChatHistory) // returns Mono<ChatbotRawResponseDto>
                .map(raw -> assembleResponse(raw, topic))
                .flatMap(response -> {
                    long latency = System.currentTimeMillis() - startTime;
                    // 1. Mongo 저장 (채팅 로그 기록)
                    Mono<Void> logMono = saveMongoLog(userId, requestDto, "model", response);

                    // 2. 임베딩 저장 (중복 체크 포함)
                    Mono<Void> embeddingMono = saveEmbeddingIfNeeded(requestDto.getMessage());

                    // 3. Elastic 로그 저장
                    Mono<Void> elasticMono = saveElasticsearchLog(userId, requestDto, response, topic, latency);

                    // 병렬 수행 후 원래 응답 반환
                    return Mono.when(logMono, embeddingMono, elasticMono)
                            .thenReturn(response);
                });
    }

    private String generatePromptByTopic(Topic topic) {
        PromptStrategy strategy = promptStrategyFactory.getStrategy(topic);
        List<PlanDto> plans = planService.getPlansSorted("popular");
        String plansJson = JsonUtil.toJson(plans);
        return switch (topic) {
            case RECOMMENDATION_PLAN -> "사용자의 요금제 이용 패턴에 맞는 요금제를 추천해줘.";

            case PLAN_DETAIL, PLAN_LIST, COMPARE_PLAN -> invokeSingleArgStrategy(strategy, plansJson);

            case INFO -> invokeNoArgsStrategy(strategy);

            default -> invokeNoArgsStrategy(strategy); // 기타 토픽도 NoArgs로 처리
        };
    }

    private ChatResponseDto assembleResponse(ChatbotRawResponseDto raw, Topic topic) {
        List<Button> buttons = createButtons(topic);
        List<Card> cards = createCards(raw.getPlanIds());
        return ChatResponseDto.builder()
                .type(ChatResponseType.MAIN_REPLY)
                .message(raw.getReply())
                .buttons(buttons)
                .cards(cards)
                .build();
    }

    private List<Card> createCards(List<String> planIds) {
        if (planIds == null || planIds.isEmpty()) return List.of();

        return planIds.stream()
                .map(planService::getPlanDetail)
                .map(plan -> Card.builder()
                        .value(plan)
                        .build())
                .toList();
    }

    private List<Button> createButtons(Topic topic) {
        if (List.of(Topic.INFO, Topic.RECOMMENDATION_PLAN, Topic.MY_USAGE_INFORMATION, Topic.ETC).contains(topic)) {
            return List.of();
        }

        return List.of(
                Button.builder()
                        .label("전체 요금 보러가기")
                        .type(ButtonType.URL)
                        .value("https://naver.com") // TODO: 실제 URL
                        .build()
        );
    }

    private Mono<Void> saveMongoLog(String userId, ChatRequestDto requestDto, String role, ChatResponseDto response) {
        return Mono.fromRunnable(() -> {
            String message = role.equals("user") ? requestDto.getMessage() : response.getMessage();
            ChatLogRequestDto logDto = ChatLogRequestDto.createChatLogRequestDto(
                    requestDto.getSessionId(), userId, role, message
            );
            chatLogService.saveRecentAndPermanentChatLog(logDto);
        });
    }

    private Mono<Void> saveEmbeddingIfNeeded(String message) {
        return embeddingService.alreadyExists(message)
                .flatMap(exists -> {
                    if (exists) {
                        log.info("Already embedded.");
                        return Mono.empty();
                    } else {
                        log.info("New question, embedding...");
                        embeddingService.indexWithEmbedding(message);
                        return Mono.empty();
                    }
                });
    }


    private Mono<Void> saveElasticsearchLog(String userId, ChatRequestDto requestDto, ChatResponseDto response, Topic topic, long latency) {
        return Mono.fromRunnable(() -> {
            try {
                if (response == null || topic == null) return; // 사용자 메시지 저장 시 무시
                ChatLogDto chatLogDto = new ChatLogDto(
                        userId,
                        requestDto.getSessionId(),
                        Instant.now(),
                        requestDto.getMessage(),
                        topic,
                        response.getMessage(),
                        null,
                        null,
                        latency
                );
                elasticsearchLogService.saveChatLog(chatLogDto);
            } catch (Exception e) {
                throw new RuntimeException("Chat log 저장 중 오류 발생", e);
            }
        });
    }
}
