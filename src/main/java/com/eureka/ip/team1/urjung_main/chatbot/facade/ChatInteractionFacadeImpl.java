package com.eureka.ip.team1.urjung_main.chatbot.facade;

import com.eureka.ip.team1.urjung_main.chatbot.dto.Button;
import com.eureka.ip.team1.urjung_main.chatbot.dto.Card;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ButtonType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.CardType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.generator.PromptStrategyFactory;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.*;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatBotService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordService;
import com.eureka.ip.team1.urjung_main.chatbot.utils.JsonUtil;
import com.eureka.ip.team1.urjung_main.embedding.service.EmbeddingService;
import com.eureka.ip.team1.urjung_main.log.dto.ChatLogDto;
import com.eureka.ip.team1.urjung_main.log.service.ElasticsearchLogService;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
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
                .flatMap(raw -> {
                    // 가공: raw → ChatResponseDto
                    ChatResponseDto.ChatResponseDtoBuilder builder = ChatResponseDto.builder()
                            .message(raw.getReply());

                    List<Button> buttons = new ArrayList<>();

                    // 1. 전체 요금제 바로가기 버튼
                    if (topic != Topic.INFO && topic != Topic.RECOMMENDATION_PLAN && topic != Topic.MY_USAGE_INFORMATION && topic != Topic.ETC) {
                        buttons.add(Button.builder()
                                .label("전체 요금 보러가기")
                                .type(ButtonType.URL)
                                .value("https://naver.com") // 실제 URL로 교체
                                .build());
                    }

                    List<Card> cards = new ArrayList<>();
                    // 조건 2: 추천 요금제가 존재하면 planId 기준으로 버튼 생성
                    if (raw.getPlanIds() != null && !raw.getPlanIds().isEmpty()) {
                        List<PlanDetailDto> list = raw.getPlanIds().stream()
                                .map(id -> planService.getPlanDetail(id))
                                .toList();

                        cards.addAll(list.stream()
                                .map(plan -> Card.builder()
                                        .type(CardType.PLAN)
                                        .value(plan)
                                        .build())
                                .toList());
                    }

                    ChatResponseDto response = builder
                            .buttons(buttons)
                            .cards(cards)
                            .build();

                    long endTime = System.currentTimeMillis();
                    long latency = endTime - startTime;
                    // 저장 후 반환
                    return embeddingService.alreadyExists(requestDto.getMessage())
                            .flatMap(exists -> {
                                if (!exists) {
                                    log.info("new question");
                                    return embeddingService.indexWithEmbedding(requestDto.getMessage())
                                            .then(Mono.defer(() -> {
                                                try {
                                                    return saveChatLog(userId, requestDto, response, topic, latency);
                                                } catch (IOException e) {
                                                    return Mono.error(e);
                                                }
                                            }));

                                } else {
                                    log.info("already inserted");
                                    return Mono.defer(() -> {
                                        try {
                                            return saveChatLog(userId, requestDto, response, topic, latency);
                                        } catch (IOException e) {
                                            return Mono.error(e);
                                        }
                                    });
                                }
                            })
                            .thenReturn(response)
                            .onErrorResume(e -> {
                                log.error("임베딩 저장 중 에러 발생 ㅠㅠ", e);
                                return Mono.error(new RuntimeException("임베딩 저장 중 오류 발생 ㅠㅠ", e));
                            });
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

            case INFO, ALL_PLAN_INFORMATION ->
                    invokeNoArgsStrategy(strategy);

            default ->
                    invokeNoArgsStrategy(strategy); // 기타 토픽도 NoArgs로 처리
        };
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
