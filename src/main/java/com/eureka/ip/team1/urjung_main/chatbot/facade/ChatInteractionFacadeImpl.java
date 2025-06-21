//package com.eureka.ip.team1.urjung_main.chatbot.facade;
//
//import com.eureka.ip.team1.urjung_main.chatbot.dispatcher.ChatStateDispatcher;
//import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
//import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
//import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatCommand;
//import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
//import com.eureka.ip.team1.urjung_main.chatbot.processor.ChatLogProcessor;
//import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ChatInteractionFacadeImpl implements ChatInteractionFacade {
//    private final ChatStateDispatcher dispatcher;
//    private final ChatLogProcessor chatLogProcessor;
//    private final ForbiddenWordService forbiddenWordService;
//
//    @Override
//    public Flux<ChatResponseDto> chat(String userId, ChatRequestDto requestDto) {
//        // 금칙어 필터링 우선 수행
////        if (requestDto.getCommand().equals(ChatCommand.CHAT) && forbiddenWordService.containsForbiddenWord(requestDto.getMessage())) {
////            ChatResponseDto responseDto = ChatResponseDto.builder()
////                    .message("입력할 수 없는 단어가 포함되어 있습니다.")
////                    .build();
////            return Flux.just(responseDto);
////        }
//        long start = System.currentTimeMillis();
//
//        return dispatcher.dispatch(userId, requestDto)
//                .flatMap(response -> {
//                            if (requestDto.getCommand().equals(ChatCommand.CHAT) && response.getType() != ChatResponseType.WAITING) {
//                                return Mono.when(
//                                        chatLogProcessor.saveRecentLog(userId, requestDto, response),
//                                        chatLogProcessor.savePermanentLog(userId, requestDto, response),
//                                        chatLogProcessor.saveEmbeddingIfNeeded(requestDto.getMessage()),
//                                        chatLogProcessor.saveElasticsearchLog(userId, requestDto, response, response.getTopic(), System.currentTimeMillis() - start)
//                                ).thenReturn(response); // response 그대로 다시 방출
//                            }
//                            return Mono.just(response); // 다른 응답은 그대로 통과
//                        }
//                );
//    }
//}
