package com.eureka.ip.team1.urjung_main.chatbot.handler;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.entity.ChatContext;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatBotService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatLogService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatStateService;
import com.eureka.ip.team1.urjung_main.chatbot.utils.CardFactory;
import com.eureka.ip.team1.urjung_main.chatbot.utils.JsonUtil;
import com.eureka.ip.team1.urjung_main.chatbot.utils.PromptTemplateProvider;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanService;
import com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AwaitingFeedBackHandler implements ChatStateHandler {
    private final ChatLogService chatLogService;
    private final ChatBotService chatBotService;
    private final UserService userService;
    private final ChatStateService chatStateService;
    private final PlanService planService;
    private final CardFactory cardFactory;

    @Override
    public ChatState getState() {
        return ChatState.AWAITING_ADDITIONAL_FEEDBACK;
    }

    @Override
    public Flux<ChatResponseDto> handle(String userId, ChatRequestDto requestDto) {
        String message = requestDto.getMessage();
        String sessionId = requestDto.getSessionId();

        String validatePrompt = PromptTemplateProvider.buildAdditionalFeedbackValidationPrompt(message);

        return chatBotService.handleAnalysisAnswer(validatePrompt, message)
                .flatMapMany(result -> {
                    if (!Boolean.TRUE.equals(result.getResult())) {
                        return Flux.just(ChatResponseDto.builder()
                                .message(result.getReply().trim())
                                        .type(ChatResponseType.ANALYSIS_REPLY)
                                .build());
                    }
                    return Flux.just(ChatResponseDto.builder()
                            .message(result.getReply().trim())
                                    .type(ChatResponseType.ANALYSIS_REPLY)
                            .build()).concatWith(generateRecommendationResponse(userId, sessionId, message));
                });
    }


    private Flux<ChatResponseDto> generateRecommendationResponse(String userId, String sessionId, String message) {
        UserDto user = userService.findById(userId);
        ChatContext context = chatLogService.getChatContext(sessionId);
        List<PlanDto> plans = getProcessedPlans();
        String plansJson = JsonUtil.toJson(plans);
        String usageSummary = buildUsageSummary(context.getUsages());

        int age = Period.between(user.getBirth(), LocalDate.now()).getYears();
        String finalPrompt = PromptTemplateProvider.buildFinalAnalysisByLinePrompt(user.getGender(), age,usageSummary, context.getPlanId(), message, plansJson);

        return chatStateService.setState(sessionId, ChatState.IDLE)
                .thenMany(chatBotService.handleAnalysisAnswer(finalPrompt, null)
                        .flatMapMany(finalRaw -> Flux.just(
                                ChatResponseDto.builder()
                                        .message(finalRaw.getReply().trim())
                                        .cards(cardFactory.createFromPlanIds(finalRaw.getPlanIds()))
                                        .build()
                        )));
    }

    private List<PlanDto> getProcessedPlans() {
        return planService.getPlansSorted("popular").stream()
                .map(plan -> PlanDto.builder()
                        .id(plan.getId())
                        .name(plan.getName())
                        .price(plan.getPrice())
                        .description(plan.getDescription())
                        .dataAmount(plan.getDataAmount() != null ? plan.getDataAmount() / 1024 : null)
                        .callAmount(plan.getCallAmount())
                        .smsAmount(plan.getSmsAmount())
                        .createdAt(plan.getCreatedAt())
                        .build())
                .toList();
    }

    private String buildUsageSummary(List<UsageResponseDto> usages) {
        return usages.stream()
                .map(u -> String.format("월: %s, 데이터: %dGB, 통화: %d분, 문자: %d건",
                        u.getMonth(),
                        u.getData() / 1024,
                        u.getCallMinute(),
                        u.getMessage()))
                .collect(Collectors.joining("\n"));
    }
}
