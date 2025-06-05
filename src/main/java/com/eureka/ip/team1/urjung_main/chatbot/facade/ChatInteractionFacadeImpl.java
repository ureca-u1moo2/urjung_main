package com.eureka.ip.team1.urjung_main.chatbot.facade;

import com.eureka.ip.team1.urjung_main.chatbot.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatBotService;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatInteractionFacadeImpl implements ChatInteractionFacade{
    private final ChatBotService chatBotService;

    @Override
    public ApiResponse<ChatResponseDto> chat(String userId, ChatRequestDto requestDto){
        //  금칙어 필터링
        //  사용자 메세지 저장
        //  사용자 최근 대화 내용 불러오기
        //  챗봇 응답 요청
        ChatResponseDto chatResponseDto = chatBotService.handleUserMessage(userId, requestDto);
        //  챗봇 응답 저장

        return ApiResponse.<ChatResponseDto>builder()
                .result(Result.SUCCESS)
                .data(chatResponseDto)
                .message("챗봇 응답 성공")
                .build();
    }
}
