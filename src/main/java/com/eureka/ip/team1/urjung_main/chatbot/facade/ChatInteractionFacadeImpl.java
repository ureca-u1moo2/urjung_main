package com.eureka.ip.team1.urjung_main.chatbot.facade;

import com.eureka.ip.team1.urjung_main.chatbot.dto.*;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ButtonType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.generator.PromptStrategyFactory;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.PromptStrategy;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.TopicClassifyPromptStrategy;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatBotService;
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

        long startTime = System.currentTimeMillis();

        return chatBotService.handleUserMessage(prompt, requestDto.getMessage()) // returns Mono<ChatbotRawResponseDto>
                .map(raw -> assembleResponse(raw, topic))
                .flatMap(response -> {
                    long latency = System.currentTimeMillis() - startTime;
                    return persistLogAndEmbedding(userId, requestDto, response, topic, latency)
                            .thenReturn(response);
                });
    }

    private String generatePromptByTopic(ChatRequestDto dto, Topic topic) {
        PromptStrategy strategy = promptStrategyFactory.getStrategy(topic);
        List<PlanDto> plans = planService.getPlansSorted("popular");
        String plansJson = JsonUtil.toJson(plans);
        return switch (topic) {
            case RECOMMENDATION_PLAN -> "사용자의 요금제 이용 패턴에 맞는 요금제를 추천해줘.";

            case PLAN_DETAIL, COMPARE_PLAN_WITHOUT_MY_PLAN, FILTERED_PLAN_LIST ->
                    invokeSingleArgStrategy(strategy, plansJson);

            case INFO, ALL_PLAN_INFORMATION -> invokeNoArgsStrategy(strategy);

            default -> invokeNoArgsStrategy(strategy); // 기타 토픽도 NoArgs로 처리
        };
    }

    private ChatResponseDto assembleResponse(ChatbotRawResponseDto raw, Topic topic) {
        List<Button> buttons = createButtons(topic);
        List<Card> cards = createCards(raw.getPlanIds());
        return ChatResponseDto.builder()
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

    private Mono<Void> persistLogAndEmbedding(String userId, ChatRequestDto requestDto, ChatResponseDto response, Topic topic, long latency) {
        try {
            log.info("response cards: {}", response.getCards());
            if (!embeddingService.alreadyExists(requestDto.getMessage())) {
                log.info("📌 New question, embedding...");
                embeddingService.indexWithEmbedding(requestDto.getMessage());
            } else {
                log.info("✔ Already embedded.");
            }
            return saveChatLog(userId, requestDto, response, topic, latency);
        } catch (IOException e) {
            log.error("❌ 로그 저장 실패", e);
            return Mono.empty(); // 저장 실패하더라도 응답은 반환
        }
    }

    private Mono<Void> saveChatLog(String userId, ChatRequestDto requestDto, ChatResponseDto response, Topic topic, long latency) throws IOException {
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
                        latency
                );
                elasticsearchLogService.saveChatLog(chatLogDto); // 동기 호출
            } catch (Exception e) {
                throw new RuntimeException("Chat log 저장 중 오류 발생", e);
            }
        });
    }
}
