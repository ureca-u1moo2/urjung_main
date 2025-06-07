package com.eureka.ip.team1.urjung_main.chatbot.facade;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatInteractionFacadeImpl implements ChatInteractionFacade {
    private final ChatBotService chatBotService;

    @Override
    public Flux<ChatResponseDto> chat(String userId, ChatRequestDto requestDto) {
        //  금칙어 필터링
        //  사용자 메세지 저장
        //  사용자 최근 대화 내용 불러오기
        //  챗봇 응답 요청
        Flux<ChatResponseDto> rawResponse = chatBotService.handleUserMessage(userId, requestDto);
        Flux<ChatResponseDto> loggingResponse = rawResponse
                .doOnNext(msg -> log.info("[챗봇 출력] {}", msg.getMessage()))
                .doOnComplete(() -> log.info("[챗봇 응답 완료]")
                );
        //  챗봇 응답 저장
        ArrayList<String> buffer = new ArrayList<>();
        return loggingResponse
                .doOnNext(msg -> buffer.add(msg.getMessage()))
                .doFinally(signal -> {
                    if (!buffer.isEmpty()) {
                        String finalReply = buffer.get(buffer.size() - 1);
                        log.info("[챗봇 응답 저장 완료] {}", finalReply);
                    }
                });
    }
}
