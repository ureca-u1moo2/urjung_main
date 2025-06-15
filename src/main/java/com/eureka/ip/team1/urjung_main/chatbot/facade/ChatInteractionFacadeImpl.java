package com.eureka.ip.team1.urjung_main.chatbot.facade;

import com.eureka.ip.team1.urjung_main.chatbot.dto.*;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ButtonType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.generator.PromptStrategyFactory;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.PromptStrategy;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatBotService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatLogService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatStateService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordService;
import com.eureka.ip.team1.urjung_main.chatbot.utils.JsonUtil;
import com.eureka.ip.team1.urjung_main.embedding.service.EmbeddingService;
import com.eureka.ip.team1.urjung_main.log.dto.ChatLogDto;
import com.eureka.ip.team1.urjung_main.log.service.ElasticsearchLogService;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanService;
import com.eureka.ip.team1.urjung_main.user.dto.UsageRequestDto;
import com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto;
import com.eureka.ip.team1.urjung_main.user.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    private final ChatLogService chatLogService;
    private final ChatStateService chatStateService;
    private final UsageService usageService;

    @Override
    public Flux<ChatResponseDto> chat(String userId, ChatRequestDto requestDto) {
        // 금칙어 필터링 우선 수행
        if (forbiddenWordService.containsForbiddenWord(requestDto.getMessage())) {
            ChatResponseDto responseDto = ChatResponseDto.builder()
                    .message("입력할 수 없는 단어가 포함되어 있습니다.")
                    .build();
            return Flux.just(responseDto);
        }
        // 상태 확인 후 상태에 따른 분기
        return chatStateService.getState(requestDto.getSessionId())
                .doOnNext(chatState -> log.info(chatState.name()))
                .flatMapMany(state -> routeByState(state, userId, requestDto));

    }

    private Flux<ChatResponseDto> routeByState(ChatState state, String userId, ChatRequestDto requestDto) {
        String message = requestDto.getMessage();
        String sessionId = requestDto.getSessionId();

        if (state == ChatState.WAITING_SELECT_LINE) {
            return Flux.just(ChatResponseDto.builder().message("회선 선택 기능은 아직 구현 중입니다.").build());
        }

        if (state == ChatState.WAITING_PERSONAL_ANALYSIS) {
            if ("성향 분석 시작".equals(message)) {
                return chatStateService.setState(sessionId, ChatState.PERSONAL_ANALYSIS_1)
                        .thenMany(Flux.just(
                                ChatResponseDto.builder().message("그럼 성향 분석을 시작할게요! 첫 번째 질문입니다.").build(),
                                ChatResponseDto.builder().message("1. 평소에 데이터를 얼마나 자주 사용하시나요?\n(예: 하루에 1~2시간 정도 사용해요 / 월 20GB 정도 써요)").build()
                        ));
            } else {
                return chatStateService.setState(sessionId, ChatState.DEFAULT)
                        .thenMany(Flux.just(
                                ChatResponseDto.builder().message("성향 분석이 취소되었습니다.").build(),
                                ChatResponseDto.builder().message("제가 필요하시다면 언제든 말 걸어주세요!").build()
                        ));
            }
        }

        if (state == ChatState.PERSONAL_ANALYSIS_1) {
            return handleAnalysisStep(
                    1,
                    sessionId,
                    userId,
                    message,
                    ChatState.PERSONAL_ANALYSIS_2,
                    "1. 평소에 데이터를 얼마나 자주 사용하시나요?\n(예: 하루에 1~2시간 정도 사용해요 / 월 20GB 정도 써요)",
                    "2. 해외 로밍 서비스를 얼마나 자주 사용하시나요?\n(예: 해외 출장이 많아서 한 달에 한두 번 정도 써요 / 거의 사용하지 않아요)"
            );
        }

        if (state == ChatState.PERSONAL_ANALYSIS_2) {
            return handleAnalysisStep(
                    2,
                    sessionId,
                    userId,
                    message,
                    ChatState.PERSONAL_ANALYSIS_3,
                    "2. 해외 로밍 서비스를 얼마나 자주 사용하시나요?\n(예: 해외 출장이 많아서 한 달에 한두 번 정도 써요 / 거의 사용하지 않아요)",
                    "3. 주로 어떤 용도로 휴대폰을 사용하시나요?\n(예: SNS, 유튜브, 웹서핑, 업무용 메신저 등)"
            );
        }

        if (state == ChatState.PERSONAL_ANALYSIS_3) {
            return handleAnalysisStep(
                    3,
                    sessionId,
                    userId,
                    message,
                    ChatState.DEFAULT,
                    "3. 주로 어떤 용도로 휴대폰을 사용하시나요?\n(예: SNS, 유튜브, 웹서핑, 업무용 메신저 등)",
                    null //
            );
        }

        return handleDefaultFlow(userId, requestDto);
    }

    private Flux<ChatResponseDto> handleAnalysisStep(
            int step,
            String sessionId,
            String userId,
            String userAnswer,
            ChatState nextState,
            String question,
            String nextMessage
    ) {
        String prompt = """
                당신은 통신사 요금제 추천을 위한 성향 분석 도우미입니다.
                
                아래 질문과 사용자 응답을 기반으로, 응답이 의미 있는 성향 분석 답변이면 `"reply"` 필드에 자연스러운 반응을 작성하세요.
                - 무의미한 경우에도 `"reply"`는 완곡한 유도 메시지를, `"result"`는 false로 설정하세요.
                - 절대로 추가적인 질문은 하지마세요
                
                🎯 응답 형식:
                ```json
                {
                  "reply": "자연스러운 반응 메시지",
                  "result": true 또는 false
                }
                ```
                
                질문: %s
                답변:
                """.formatted(question);

        return chatBotService.handleAnalysisAnswer(prompt, userAnswer)
                .flatMapMany(raw -> {
                    String reply = raw.getReply().trim();
                    boolean isValid = raw.getResult() != null && raw.getResult();

                    if (!isValid) {
                        return Flux.just(ChatResponseDto.builder().message(reply).build());
                    }

                    // 유효한 응답 저장
                    chatLogService.saveAnswer(sessionId, step, userAnswer, userId);

                    if (nextMessage != null) {
                        return chatStateService.setState(sessionId, nextState)
                                .thenMany(Flux.just(
                                        ChatResponseDto.builder().message(reply).build(),
                                        ChatResponseDto.builder().message(nextMessage).build()
                                ));
                    } else {
                        // 마지막 질문이므로 → 답변 저장 + 상태 전환 + 분석 요청 → 결과 포함
                        return chatStateService.setState(sessionId, nextState)
                                .then(Mono.fromCallable(() -> chatLogService.getAnalysis(sessionId)))
                                .flatMapMany(analysis -> {
                                    String a1 = analysis.getAnswers().getOrDefault(1, "");
                                    String a2 = analysis.getAnswers().getOrDefault(2, "");
                                    String a3 = analysis.getAnswers().getOrDefault(3, "");
                                    List<PlanDto> plans = planService.getPlansSorted("popular");
                                    String plansJson = JsonUtil.toJson(plans); // 요금제 목록 JSON

                                    String finalPrompt = """
                                            당신은 통신사 요금제 추천 전문가입니다.
                                            
                                            아래는 사용자와의 성향 분석 대화 내용입니다. 이 데이터를 기반으로, 해당 사용자에게 적합한 통신사 요금제 유형을 각 컬럼을 중점적으로 분석하고 추천해 주세요.
                                            
                                            🎯 반드시 아래 형식으로 JSON으로 응답하세요:
                                            {
                                              "reply": "성향 분석 결과에 대한 간결하고 친절한 메시지",
                                              "planIds": ["추천 요금제 ID1", "추천 요금제 ID2", ...]
                                            }
                                            
                                            [사용자 응답]
                                            1. 평소에 데이터를 얼마나 자주 사용하시나요?
                                            → %s
                                            
                                            2. 해외 로밍 서비스를 얼마나 자주 사용하시나요?
                                            → %s
                                            
                                            3. 주로 어떤 용도로 휴대폰을 사용하시나요?
                                            → %s
                                            
                                            📦 추천 가능한 요금제 목록 (JSON 형식)
                                            %s
                                            """.formatted(a1, a2, a3, plansJson);


                                    return chatBotService.handleAnalysisAnswer(finalPrompt, "")
                                            .flatMapMany(finalRaw -> Flux.just(
                                                    ChatResponseDto.builder().message(reply).build(), // 3번 질문에 대한 반응
                                                    ChatResponseDto.builder().message("모든 질문에 답변해주셔서 감사합니다 😊\n고객님께 어울리는 요금제를 분석해드릴께요! 잠시만 기다려주세요").build(),
                                                    ChatResponseDto.builder().message(finalRaw.getReply().trim()).
                                                            cards(createCards(finalRaw.getPlanIds())).build() // 분석 결과
                                            ));
                                });
                    }
                });
    }


    private Flux<ChatResponseDto> handleDefaultFlow(String userId, ChatRequestDto requestDto) {
        List<Content> recentChatHistory = chatLogService.getRecentChatHistory(userId, requestDto.getSessionId());
        String recentChatHistoryJson = JsonUtil.toJson(recentChatHistory);

        return chatBotService.classifyTopic(requestDto.getMessage(), recentChatHistoryJson)
                .flatMapMany(response -> {
                    chatLogService.saveRecentAndPermanentChatLog(
                            ChatLogRequestDto.createChatLogRequestDto(requestDto.getSessionId(), userId, "user", requestDto.getMessage())
                    );

                    Mono<ChatResponseDto> waitMessage = Mono.just(
                            ChatResponseDto.builder()
                                    .type(ChatResponseType.WAITING)
                                    .message(response.getWaitMessage())
                                    .build()
                    );

                    return Flux.concat(waitMessage, handleByTopic(userId, requestDto, recentChatHistoryJson, response.getTopic()));
                });
    }

    private Mono<ChatResponseDto> handleByTopic(String userId, ChatRequestDto requestDto, String recentChatHistory, Topic topic) {
        if (topic == Topic.RECOMMENDATION_PLAN) {
            //  1. WAITING_SELECT_LINE : 회선 선택
            //       - 사용자가 가입한 회선이 있는 지 판단
            List<UsageResponseDto> currentMonthUsagesByUserId = usageService.getCurrentMonthUsagesByUserId(UsageRequestDto.builder().userId(userId).build());
            if (currentMonthUsagesByUserId == null || currentMonthUsagesByUserId.isEmpty()) {
                //  - 없다면 3번으로 상태 변경
                return chatStateService.setState(requestDto.getSessionId(), ChatState.WAITING_PERSONAL_ANALYSIS)
                        .thenReturn(ChatResponseDto.builder()
                                .message("현재 가입된 회선이 없어 성향 분석을 진행할게요.")
                                .buttons(List.of(Button.builder()
                                        .label("성향 분석 진행")
                                        .value("성향 분석 시작")
                                        .type(ButtonType.INPUT_DATA)
                                        .build()))
                                .build());

            }
            return null;
            //
  /*
                1. WAITING_SELECT_LINE : 회선 선택
                    - 사용자가 가입한 회선이 있는 지 판단
                        -
                        - 있다면 2번으로 상태 변경
                2. LINE_ANALYSIS : 선택한 회선 기반 판단
                     - 해당 회선의 사용이력이 최근 3개월 이상인지 확인
                        - 없다면 3번으로 상태 변경
                        - 있다면 3개월 기반 평균저장 후 4-N 으로 이동

                3. READY_PERSONAL_ANALYSIS : 성향 분석 선택 대기
                      - 버튼을 누르거나 성향분석을 하겠다는 의사를 밝히면 성향분석 시작
                      - 없다면 상태 DEFAULT로 초기화

                4. PERSONAL_ANALYSIS 1~N : N가지의 성향 분석
                       - 각 항목에 대한 질문을 던지고 응답 저장
                 */

        } else {
            String prompt = generatePromptByTopic(topic, userId);

            long startTime = System.currentTimeMillis();


            return chatBotService.handleUserMessage(prompt, requestDto.getMessage(), recentChatHistory) // returns Mono<ChatbotRawResponseDto>
                    .map(raw -> assembleResponse(raw, topic, userId))
                    .flatMap(response -> {
                        List<Mono<Void>> sideEffects = new ArrayList<>();

                        if (response.getLineSelectButton() != null) {
                            Mono<Void> stateMono = chatStateService.setState(requestDto.getSessionId(), ChatState.WAITING_SELECT_LINE).then();
                            sideEffects.add(stateMono);
                        }

                        Mono<Void> logMono = saveMongoLog(userId, requestDto, "model", response);
                        Mono<Void> embeddingMono = saveEmbeddingIfNeeded(requestDto.getMessage());
                        Mono<Void> elasticMono = saveElasticsearchLog(userId, requestDto, response, topic, System.currentTimeMillis() - startTime);

                        sideEffects.add(logMono);
                        sideEffects.add(embeddingMono);
                        sideEffects.add(elasticMono);

                        return Mono.when(sideEffects).thenReturn(response);
                    });
        }
    }

    private String generatePromptByTopic(Topic topic, String userId) {
        PromptStrategy strategy = promptStrategyFactory.getStrategy(topic);

        return switch (topic) {
            case PLAN_DETAIL, PLAN_LIST, COMPARE_PLAN -> {
                List<PlanDto> plans = planService.getPlansSorted("popular");
                String plansJson = JsonUtil.toJson(plans);
                yield invokeSingleArgStrategy(strategy, plansJson);
            }

            case INFO, COMPARE_WITH_MY_PLAN -> invokeNoArgsStrategy(strategy);

            default -> invokeNoArgsStrategy(strategy); // 기타 토픽도 NoArgs로 처리
        };
    }

    private ChatResponseDto assembleResponse(ChatbotRawResponseDto raw, Topic topic, String userId) {
        List<Button> buttons = createButtons(topic);
        List<Card> cards = createCards(raw.getPlanIds());
        LineSelectButton lineSelectButton = null;
        if (raw.getNeedSelectLine() != null && raw.getNeedSelectLine()) {
            lineSelectButton = createLineSelectButton(userId);
        }

        return ChatResponseDto.builder()
                .type(ChatResponseType.MAIN_REPLY)
                .message(raw.getReply())
                .buttons(buttons)
                .cards(cards)
                .lineSelectButton(lineSelectButton)
                .build();
    }

    private LineSelectButton createLineSelectButton(String userId) {
        // 사용자 아이디 파라미터 넘기면 -> 휴대폰 번호 리스트로 반환
        List<UsageResponseDto> currentMonthUsagesByUserId = usageService.getCurrentMonthUsagesByUserId(UsageRequestDto.builder().userId(userId).build());
        List<String> phoneNumbers = currentMonthUsagesByUserId.stream()
                .map(responseDto -> responseDto.getPhoneNumber())
                .toList();

        return LineSelectButton.builder().phoneNumbers(phoneNumbers).build();

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

        String label = "요금제 페이지 바로가기";
        if (topic == Topic.PLAN_DETAIL || topic == Topic.COMPARE_PLAN || topic == Topic.PLAN_LIST)
            label = "더 많은 요금제 보러가기";

        return List.of(
                Button.builder()
                        .label(label)
                        .type(ButtonType.URL)
                        .value("https://naver.com") // TODO: 실제 URL
                        .build()
        );
    }

    private Mono<Void> saveMongoLog(String userId, ChatRequestDto requestDto, String role, ChatResponseDto response) {
        return Mono.fromRunnable(() -> {
            String message = role.equals("user") ? requestDto.getMessage() : response.getMessage();
            if (response.getCards().size() > 0) {
                List<PlanDetailDto> planList = response.getCards().stream()
                        .map(card -> card.getValue())
                        .toList();

                message += "\n 반환한 요금제 :" + JsonUtil.toJson(planList);
            }
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
