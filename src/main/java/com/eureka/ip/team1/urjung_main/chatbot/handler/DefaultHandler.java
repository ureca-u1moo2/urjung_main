package com.eureka.ip.team1.urjung_main.chatbot.handler;

import com.eureka.ip.team1.urjung_main.chatbot.component.Button;
import com.eureka.ip.team1.urjung_main.chatbot.component.Card;
import com.eureka.ip.team1.urjung_main.chatbot.component.LineSelectButton;
import com.eureka.ip.team1.urjung_main.chatbot.dto.*;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.generator.PromptStrategyFactory;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.strategy.PromptStrategy;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatBotService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatLogService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatStateService;
import com.eureka.ip.team1.urjung_main.chatbot.utils.JsonUtil;
import com.eureka.ip.team1.urjung_main.chatbot.utils.PlanProvider;
import com.eureka.ip.team1.urjung_main.chatbot.utils.PromptStrategyInvoker;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanService;
import com.eureka.ip.team1.urjung_main.user.dto.LineDto;
import com.eureka.ip.team1.urjung_main.user.service.LineSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;


@Component
@Primary
@RequiredArgsConstructor
public class DefaultHandler implements ChatStateHandler {

    private final ChatBotService chatBotService;
    private final ChatLogService chatLogService;
    private final ChatStateService chatStateService;
    private final PlanProvider planProvider;
    private final LineSubscriptionService lineSubscriptionService;
    private final PromptStrategyFactory promptStrategyFactory;

    @Override
    public ChatState getState() {
        return ChatState.IDLE;
    }

    @Override
    public Flux<ChatResponseDto> handle(String userId, ChatRequestDto requestDto) {
        String recentChatHistory =
                JsonUtil.toJson(chatLogService.getRecentChatHistory(userId, requestDto.getSessionId()));

        return chatBotService.classifyTopic(requestDto.getMessage(), recentChatHistory)
                .flatMapMany(result -> {
                    Mono<ChatResponseDto> waitMessage = buildWaitMessage(result);
                    if (result.getTopic() == Topic.ALL_PLAN_LIST) {
                        return waitMessage.concatWith(handleAllPlanTopic(requestDto));
                    }
                    return waitMessage.concatWith(handleGeneralTopic(result.getTopic(), requestDto, recentChatHistory));
                });
    }

    private Mono<ChatResponseDto> buildWaitMessage(ClassifiedTopicResult result) {
        return Mono.just(ChatResponseDto.builder()
                .type(ChatResponseType.WAITING)
                .message(result.getWaitMessage())
                .build());
    }

    private Flux<ChatResponseDto> handleAllPlanTopic(ChatRequestDto requestDto) {
        return chatBotService.handleUserMessage(generatePromptByTopic(Topic.ALL_PLAN_LIST), requestDto.getMessage(), null)
                .flatMapMany(raw -> {
                    // 전체 요금제 목록 직접 생성
                    List<PlanDto> plans = planProvider.getPlans();
                    List<String> planIds = plans.stream()
                            .map(PlanDto::getId)
                            .collect(Collectors.toList());

                    return Flux.just(ChatResponseDto.ofMainReply(raw.getReply().trim(),createCards(planIds),Topic.ALL_PLAN_LIST));
                });
    }


    private Flux<ChatResponseDto> handleGeneralTopic(Topic topic, ChatRequestDto requestDto, String chatHistoryJson) {
        // 일반 토픽 처리
        return chatBotService.handleUserMessage(generatePromptByTopic(topic), requestDto.getMessage(), chatHistoryJson)
                .flatMapMany(raw -> Flux.just(
                    ChatResponseDto.ofMainReply(raw.getReply().trim(),createCards(raw.getPlanIds()),Topic.ALL_PLAN_LIST)
                        )
                );
    }

    private String generatePromptByTopic(Topic topic) {
        PromptStrategy strategy = promptStrategyFactory.getStrategy(topic);
        return switch (topic) {
            case PLAN_DETAIL, PLAN_LIST, COMPARE_PLAN, RECOMMENDATION_PLAN -> {
                List<PlanDto> plans = planProvider.getPlans();
                String plansJson = JsonUtil.toJson(plans);
                yield PromptStrategyInvoker.invokeSingleArgStrategy(strategy, plansJson);
            }

            default -> PromptStrategyInvoker.invokeNoArgsStrategy(strategy);
        };
    }

    private List<Card> createCards(List<String> planIds) {
        if (planIds == null || planIds.isEmpty()) return List.of();
        return planIds.stream()
                .map(planProvider::getPlanById)
                .map(plan -> Card.builder().value(plan).build())
                .toList();
    }
}
