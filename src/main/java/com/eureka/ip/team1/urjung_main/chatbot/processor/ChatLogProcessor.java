package com.eureka.ip.team1.urjung_main.chatbot.processor;

import com.eureka.ip.team1.urjung_main.chatbot.component.Card;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatLogRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
import com.eureka.ip.team1.urjung_main.chatbot.enums.ChatResponseType;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.chatbot.service.ChatLogService;
import com.eureka.ip.team1.urjung_main.chatbot.utils.JsonUtil;
import com.eureka.ip.team1.urjung_main.embedding.service.EmbeddingService;
import com.eureka.ip.team1.urjung_main.log.dto.ChatLogDto;
import com.eureka.ip.team1.urjung_main.log.service.ElasticsearchLogService;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatLogProcessor {

    private final ChatLogService chatLogService;
    private final EmbeddingService embeddingService;
    private final ElasticsearchLogService elasticsearchLogService;

    public Mono<Void> saveRecentLog(String userId, ChatRequestDto requestDto, ChatResponseDto response) {
        return Mono.fromRunnable(() -> {
            // response 타입이 main 일때는 다 저장
            String userMessage = null;
            String modelMessage = null;
            if(response.getType().equals(ChatResponseType.MAIN_REPLY)){
                userMessage = createUserLogMessage(requestDto);
                modelMessage = createModelLogMessage(response);
            }
            // response 타입이 Analysis이고 Card가 있다면 reponse 만 저장
            if (response.getType().equals(ChatResponseType.ANALYSIS_REPLY)
                    && response.getCards() != null && !response.getCards().isEmpty()) {
                modelMessage = "[요금제 추천 모드 사용 결과] : " + createModelLogMessage(response);
            }

            if(userMessage!=null){
                ChatLogRequestDto userLog = ChatLogRequestDto.createChatUserLogRequestDto(requestDto.getSessionId(), userId, userMessage);
                chatLogService.saveRecentChatLog(userLog);
            }

            if(modelMessage!=null){
                ChatLogRequestDto modelLog = ChatLogRequestDto.createChatModelLogRequestDto(requestDto.getSessionId(), userId, modelMessage);
                chatLogService.saveRecentChatLog(modelLog);
            }
        });
    }

    public Mono<Void> savePermanentMongoLog(String userId, ChatRequestDto requestDto, ChatResponseDto response) {
        return Mono.fromRunnable(() -> {
            String userMessage = createUserLogMessage(requestDto);
            String modelMessage = createModelLogMessage(response);
            if(userMessage!=null){
                ChatLogRequestDto userLog = ChatLogRequestDto.createChatUserLogRequestDto(requestDto.getSessionId(), userId, userMessage);
                chatLogService.savePermanentChatLog(userLog);
            }

            if(modelMessage!=null){
                ChatLogRequestDto modelLog = ChatLogRequestDto.createChatModelLogRequestDto(requestDto.getSessionId(), userId, modelMessage);
                chatLogService.savePermanentChatLog(modelLog);
            }
        });
    }

    private String createUserLogMessage(ChatRequestDto requestDto) {
        return requestDto.getMessage();
    }

    private String createModelLogMessage(ChatResponseDto response) {
        if (response == null) return "";

        String message = response.getMessage();

        if (response.getCards() != null && !response.getCards().isEmpty()) {

            List<PlanDto> plans = response.getCards().stream()
                    .map(Card::getValue)
                    .toList();

            message += "\n반환한 요금제: " + JsonUtil.toJson(plans);
        }

        return message;
    }

    public Mono<Void> saveEmbeddingIfNeeded(String message) {
        return embeddingService.alreadyExists(message)
                .flatMap(exists -> exists ? Mono.empty() : embeddingService.indexWithEmbedding(message));
    }

    public Mono<Void> saveElasticsearchLog(String userId, ChatRequestDto requestDto, ChatResponseDto response, Topic topic, long latency) {
        return Mono.fromRunnable(() -> {

            // 카드 이름 목록 추출
            List<String> recommendedItems = Optional.ofNullable(response.getCards())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(card -> card.getValue().getName())
                    .collect(Collectors.toList());

            ChatLogDto log = new ChatLogDto(
                    userId,
                    requestDto.getSessionId(),
                    Instant.now(),
                    requestDto.getMessage(),
                    topic,
                    response.getMessage(),
                    recommendedItems.isEmpty() ? null : recommendedItems,
                    null,
                    latency
            );
            try {
                elasticsearchLogService.saveChatLog(log);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }
}

