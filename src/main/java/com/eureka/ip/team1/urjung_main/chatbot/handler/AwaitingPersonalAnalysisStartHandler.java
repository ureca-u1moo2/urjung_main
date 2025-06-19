package com.eureka.ip.team1.urjung_main.chatbot.handler;

import com.eureka.ip.team1.urjung_main.chatbot.component.Button;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatState;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatStateService;
import com.eureka.ip.team1.urjung_main.chatbot.utils.PersonalAnalysisQuestionProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AwaitingPersonalAnalysisStartHandler implements ChatStateHandler {

    private final ChatStateService chatStateService;
    private final PersonalAnalysisQuestionProvider questionProvider;

    @Override
    public ChatState getState() {
        return ChatState.AWAITING_PERSONAL_ANALYSIS_START;
    }

    @Override
    public Flux<ChatResponseDto> handle(String userId, ChatRequestDto requestDto) {
        String sessionId = requestDto.getSessionId();
        String message = requestDto.getMessage();
        if ("취소".equals(message)) {
            return chatStateService.setState(sessionId, ChatState.IDLE)
                    .thenMany(Flux.just(
                            ChatResponseDto.builder()
                                    .type(ChatResponseType.INFO)
                                    .buttons(List.of(Button.planPage(), Button.recommendStart()))
                                    .message("성향 분석이 취소되었습니다. 다른 도움이 필요하시면 언제든지 말씀해주세요 😊")
                                    .build()
                    ));
        }

        return chatStateService.setState(sessionId, ChatState.PERSONAL_ANALYSIS)
                .thenMany(Flux.just(
                                ChatResponseDto.ofInfoReply("그럼 성향 분석을 시작할게요! 첫 번째 질문입니다.", null),
                                ChatResponseDto.ofAnalysisReply(questionProvider.getQuestion(0))
                        ).delayElements(Duration.ofSeconds(1))
                );
    }
}
