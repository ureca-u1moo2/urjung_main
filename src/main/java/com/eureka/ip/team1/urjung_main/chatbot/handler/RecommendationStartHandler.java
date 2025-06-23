package com.eureka.ip.team1.urjung_main.chatbot.handler;

import com.eureka.ip.team1.urjung_main.chatbot.component.Button;
import com.eureka.ip.team1.urjung_main.chatbot.component.LineSelectButton;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatStateService;
import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordService;
import com.eureka.ip.team1.urjung_main.common.exception.InvalidInputException;
import com.eureka.ip.team1.urjung_main.user.dto.LineDto;
import com.eureka.ip.team1.urjung_main.user.service.LineSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationStartHandler implements ChatStateHandler {

    private final LineSubscriptionService lineSubscriptionService;
    private final ChatStateService chatStateService;
    private final ForbiddenWordService forbiddenWordService;

    @Override
    public ChatState getState() {
        return ChatState.RECOMMENDATION_START;
    }

    @Override
    public Flux<ChatResponseDto> handle(String userId, ChatRequestDto requestDto) {

        // 1️⃣ 사용자의 입력 메시지만 필터링 대상
        String message = requestDto.getMessage();

        // 2️⃣ 금칙어 필터링 - 가장 먼저 수행
        if (forbiddenWordService.containsForbiddenWord(message)) {
            return Flux.just(ChatResponseDto.ofInfoReply(
                    "금칙어가 포함된 메시지는 전송할 수 없습니다.",
                    List.of(Button.cancel())
            ));
        }

        List<String> lines = lineSubscriptionService.getAllLinesByUserId(userId)
                .stream()
                .map(LineDto::getPhoneNumber)
                .toList();

        if (lines.isEmpty()) {
            return chatStateService.setState(requestDto.getSessionId(), ChatState.AWAITING_PERSONAL_ANALYSIS_START)
                    .thenMany(Flux.just(
                                    ChatResponseDto.ofInfoReply("현재 가입된 회선이 없어 성향 분석을 진행할게요.",
                                            List.of(Button.personalAnalysisStart(),Button.cancel())
                                    )
                            )
                    );
        }

        return chatStateService.setState(requestDto.getSessionId(), ChatState.AWAITING_LINE_SELECTION)
                .thenMany(Flux.just(ChatResponseDto.builder()
                        .message("추천받을 회선을 선택해주세요. 또는 성향 분석을 원하시면 버튼을 눌러주세요.")
                        .type(ChatResponseType.INFO)
                        .buttons(List.of(Button.personalAnalysisStart(),Button.cancel()))
                        .lineSelectButton(LineSelectButton.of(lines))
                        .build()));
    }
}