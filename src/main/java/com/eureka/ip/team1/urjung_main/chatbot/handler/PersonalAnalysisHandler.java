package com.eureka.ip.team1.urjung_main.chatbot.handler;

import com.eureka.ip.team1.urjung_main.chatbot.component.Button;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatbotRawResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.PlanForLLMDto;
import com.eureka.ip.team1.urjung_main.chatbot.entity.UserChatAnalysis;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatBotService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatLogService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatStateService;
import com.eureka.ip.team1.urjung_main.chatbot.utils.*;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonalAnalysisHandler implements ChatStateHandler {

    private final ChatStateService chatStateService;
    private final ChatLogService chatLogService;
    private final ChatBotService chatBotService;
    private final CardFactory cardFactory;
    private final PersonalAnalysisQuestionProvider questionProvider;
    private final PlanProvider planProvider;
    private final UserService userService;

    @Override
    public ChatState getState() {
        return ChatState.PERSONAL_ANALYSIS;
    }

    @Override
    public Flux<ChatResponseDto> handle(String userId, ChatRequestDto requestDto) {
        String sessionId = requestDto.getSessionId();
        String message = requestDto.getMessage();

        UserChatAnalysis analysis = getOrCreateAnalysis(sessionId, userId);

        if (isAnalysisCompleted(analysis)) {
            chatLogService.clearAnalysis(sessionId);
            analysis = getOrCreateAnalysis(sessionId, userId);
        }

        return processAnalysisStep(sessionId, userId, message, analysis);
    }

    private UserChatAnalysis getOrCreateAnalysis(String sessionId, String userId) {
        UserChatAnalysis analysis = chatLogService.getAnalysis(sessionId);
        if (analysis == null) {
            analysis = UserChatAnalysis.builder()
                    .sessionId(sessionId)
                    .userId(userId)
                    .currentStep(0)
                    .answers(new java.util.HashMap<>())
                    .build();
        }
        return analysis;
    }

    private boolean isAnalysisCompleted(UserChatAnalysis analysis) {
        return analysis.getCurrentStep() >= questionProvider.total();
    }

    private Flux<ChatResponseDto> processAnalysisStep(String sessionId, String userId, String message, UserChatAnalysis analysis) {
        int currentStep = analysis.getCurrentStep();
        int totalSteps = questionProvider.total();
        String questionText = questionProvider.getQuestion(currentStep);

        log.info("PersonalAnalysis - sessionId: {}, currentStep: {}, totalSteps: {}, message: {}",
                sessionId, currentStep, totalSteps, message);

        String validationPrompt = PromptTemplateProvider.buildPersonalValidationPrompt(questionText);

        return chatBotService.handleAnalysisAnswer(validationPrompt, message)
                .doOnNext(result -> log.info("Validation result - sessionId: {}, result: {}, reply: {}",
                        sessionId, result.getResult(), result.getReply()))
                .flatMapMany(validationResult -> processValidationResult(sessionId, userId, message, currentStep, totalSteps, validationResult));
    }

    private Flux<ChatResponseDto> processValidationResult(String sessionId, String userId, String message,
                                                          int currentStep, int totalSteps, ChatbotRawResponseDto validationResult) {
        if (!Boolean.TRUE.equals(validationResult.getResult())) {
            return handleInvalidAnswer(sessionId, currentStep, validationResult);
        }

        return handleValidAnswer(sessionId, userId, message, currentStep, totalSteps, validationResult);
    }

    private Flux<ChatResponseDto> handleInvalidAnswer(String sessionId, int currentStep, ChatbotRawResponseDto validationResult) {
        log.info("Invalid answer - sessionId: {}, staying at step: {}", sessionId, currentStep);
        String questionText = questionProvider.getQuestion(currentStep);

        ChatResponseDto feedback = ChatResponseDto.ofFeedBack(validationResult.getReply());
        ChatResponseDto retryQuestion = ChatResponseDto.ofAnalysisReply(questionText);

        return Flux.concat(
                Mono.just(feedback),
                Mono.delay(Duration.ofSeconds(1)) // 💡 딜레이
                        .thenReturn(retryQuestion)
        );
    }

    private Flux<ChatResponseDto> handleValidAnswer(String sessionId, String userId, String message,
                                                    int currentStep, int totalSteps, ChatbotRawResponseDto validationResult) {
        saveAnswerAndUpdateStep(sessionId, message, userId, currentStep);
        int nextStep = currentStep + 1;

        if (nextStep < totalSteps) {
            return handleNextQuestion(nextStep, validationResult);
        }

        return handleAnalysisCompletion(sessionId, userId, validationResult);
    }

    private void saveAnswerAndUpdateStep(String sessionId, String message, String userId, int currentStep) {
        chatLogService.saveAnswer(sessionId, message, userId);
        int nextStep = currentStep + 1;
        chatLogService.saveCurrentStep(sessionId, nextStep);
        log.info("Answer saved - sessionId: {}, nextStep: {}", sessionId, nextStep);
    }

    private Flux<ChatResponseDto> handleNextQuestion(int nextStep, ChatbotRawResponseDto validationResult) {
        String nextQuestion = questionProvider.getQuestion(nextStep);

        ChatResponseDto feedback = ChatResponseDto.ofFeedBack(validationResult.getReply());
        ChatResponseDto question = ChatResponseDto.ofAnalysisReply(nextQuestion);

        return Flux.concat(
                Mono.just(feedback),
                Mono.delay(Duration.ofSeconds(1)) // 💡 딜레이 추가
                        .thenReturn(question)
        );
    }

    private Flux<ChatResponseDto> handleAnalysisCompletion(String sessionId, String userId, ChatbotRawResponseDto validationResult) {
        UserDto userDto = userService.findById(userId);
        return chatStateService.setState(sessionId, ChatState.IDLE)
                .thenMany(
                        Flux.concat(
                                // 먼저 대기 메시지 전송
                                Flux.just(ChatResponseDto.ofWaitingReply("모든 질문에 답변해주셔서 감사합니다 😊\n고객님께 어울리는 요금제를 분석해드릴게요! 잠시만 기다려주세요"))
                                ,

                                // 이후 AI 분석 → 최종 응답 반환
                                Mono.fromCallable(() -> chatLogService.getAnalysis(sessionId))
                                        .flatMapMany(analysisResult -> processAnalysisResult(userDto, validationResult, analysisResult))
                        )
                );
    }

    private Flux<ChatResponseDto> processAnalysisResult(UserDto userDto, ChatbotRawResponseDto validationResult,
                                                        UserChatAnalysis analysisResult) {
        String finalPrompt = createFinalAnalysisPrompt(userDto, analysisResult);
        log.info(finalPrompt);
        return chatBotService.requestRecommendationByAnalysis(finalPrompt)
                .flatMapMany(finalRaw -> createFinalResponse(validationResult, finalRaw));
    }

    private String createFinalAnalysisPrompt(UserDto userDto, UserChatAnalysis analysisResult) {
        List<String> answers = new ArrayList<>();
        for (int i = 0; i < questionProvider.total(); i++) {
            answers.add(analysisResult.getAnswers().getOrDefault(i, ""));
        }

        List<PlanDto> plans = planProvider.getPlans();
        List<PlanForLLMDto> planForLLM = PlanLLMConverter.convertToLLMDto(plans);
        String plansJson = JsonUtil.toJson(planForLLM);

        return PromptTemplateProvider.buildFinalAnalysisPrompt(userDto, answers, questionProvider.getQuestions(), plansJson);
    }

    private Flux<ChatResponseDto> createFinalResponse(ChatbotRawResponseDto validationResult, ChatbotRawResponseDto finalRaw) {
        return Flux.just(ChatResponseDto.builder()
                .type(ChatResponseType.ANALYSIS_REPLY)
                .message(finalRaw.getReply().trim())
                .buttons(List.of(Button.planPage(), Button.recommendStart()))
                .cards(cardFactory.createFromPlanIds(finalRaw.getPlanIds()))
                .build());
    }
}