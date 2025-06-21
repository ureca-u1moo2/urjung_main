package com.eureka.ip.team1.urjung_main.chatbot.handler;

import com.eureka.ip.team1.urjung_main.chatbot.component.Button;
import com.eureka.ip.team1.urjung_main.chatbot.component.LineSelectButton;
import com.eureka.ip.team1.urjung_main.chatbot.dto.*;
import com.eureka.ip.team1.urjung_main.chatbot.entity.ChatContext;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatLogService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatStateService;
import com.eureka.ip.team1.urjung_main.user.dto.LineDto;
import com.eureka.ip.team1.urjung_main.user.dto.UsageRequestDto;
import com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto;
import com.eureka.ip.team1.urjung_main.user.service.LineSubscriptionService;
import com.eureka.ip.team1.urjung_main.user.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AwaitingLineSelectionHandler implements ChatStateHandler {

    private final UsageService usageService;
    private final ChatStateService chatStateService;
    private final ChatLogService chatLogService;
    private final LineSubscriptionService lineSubscriptionService;

    @Override
    public ChatState getState() {
        return ChatState.AWAITING_LINE_SELECTION;
    }

    @Override
    public Flux<ChatResponseDto> handle(String userId, ChatRequestDto requestDto) {
        String sessionId = requestDto.getSessionId();
        String message = requestDto.getMessage();

        return handleLineSelection(userId, sessionId, message);
    }

    private Flux<ChatResponseDto> handleLineSelection(String userId, String sessionId, String phoneNumber) {
        List<UsageResponseDto> usages = getRecentUsage(userId, phoneNumber);
        List<String> allPhones = getAllPhoneNumbers(userId);

        if (!isLineUsable(usages)) {
            return Flux.just(buildInsufficientUsageResponse(allPhones));
        }

        saveChatContext(sessionId, phoneNumber, usages);
        return buildValidUsageResponse(sessionId);
    }

    private List<UsageResponseDto> getRecentUsage(String userId, String phoneNumber) {
        return usageService.getRecent3MonthsUsagesByUserIdAndPhoneNumber(
                UsageRequestDto.builder().userId(userId).phoneNumber(phoneNumber).build());
    }

    private List<String> getAllPhoneNumbers(String userId) {
        return lineSubscriptionService.getAllLinesByUserId(userId).stream()
                .map(LineDto::getPhoneNumber)
                .toList();
    }

    private boolean isLineUsable(List<UsageResponseDto> usages) {
        return usages != null && usages.size() >= 3;
    }

    private ChatResponseDto buildInsufficientUsageResponse(List<String> allPhoneNumbers) {
        return ChatResponseDto.builder()
                .type(ChatResponseType.INFO)
                .message("해당 회선은 최근 3개월 사용내역이 부족하여 추천드리기 어렵습니다.\n다른 회선을 선택하시거나 성향 분석을 진행해주세요!")
                .buttons(List.of(Button.personalAnalysisStart(), Button.cancel()))
                .lineSelectButton(LineSelectButton.of(allPhoneNumbers))
                .build();
    }

    private void saveChatContext(String sessionId, String phoneNumber, List<UsageResponseDto> usages) {
        chatLogService.saveChatContext(sessionId, ChatContext.builder()
                .sessionId(sessionId)
                .phoneNumber(phoneNumber)
                .planId(usages.get(0).getPlanId())
                .usages(usages)
                .build());
    }

    private Flux<ChatResponseDto> buildValidUsageResponse(String sessionId) {
        return chatStateService.setState(sessionId, ChatState.AWAITING_ADDITIONAL_FEEDBACK)
                .thenMany(Flux.just(ChatResponseDto.ofAnalysisReply("현재 요금제를 사용하시면서 부족하거나 불필요한 점이 있다면 편하게 말씀해주세요!")));
    }
}
