package com.eureka.ip.team1.urjung_main.chatbot.processor;

import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatCommand;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.forbiddenword.service.ForbiddenWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class ForbiddenWordProcessor {
    private final ForbiddenWordService forbiddenWordService;

    public Flux<ChatResponseDto> filter(ChatRequestDto requestDto) {
        if (requestDto.getCommand() == ChatCommand.CHAT &&
                forbiddenWordService.containsForbiddenWord(requestDto.getMessage())) {

            ChatResponseDto responseDto = ChatResponseDto.builder()
                    .message("입력할 수 없는 단어가 포함되어 있습니다.")
                    .type(ChatResponseType.MAIN_REPLY)
                    .build();
            return Flux.just(responseDto);
        }
        return Flux.empty();
    }
}
