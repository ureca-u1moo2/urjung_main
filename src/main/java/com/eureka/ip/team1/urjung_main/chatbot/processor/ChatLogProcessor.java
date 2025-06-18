package com.eureka.ip.team1.urjung_main.chatbot.processor;

import com.eureka.ip.team1.urjung_main.chatbot.component.Card;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatLogRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatRequestDto;
import com.eureka.ip.team1.urjung_main.chatbot.dto.ChatResponseDto;
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

    public Mono<Void> saveMongoLog(String userId, ChatRequestDto requestDto, String role, ChatResponseDto response) {
        return Mono.fromRunnable(() -> {
            if(role.equals("model"))
                log.info("모델들어옴");
            String message = role.equals("user") ? requestDto.getMessage() : response.getMessage();

            if (response!=null&&response.getCards() != null && !response.getCards().isEmpty()) {
                List<PlanDto> plans = response.getCards().stream()
                        .map(Card::getValue)
                        .toList();

                message += "\n반환한 요금제: " + JsonUtil.toJson(plans);
            }

            ChatLogRequestDto logDto = ChatLogRequestDto.createChatLogRequestDto(
                    requestDto.getSessionId(), userId, role, message
            );
            chatLogService.saveRecentAndPermanentChatLog(logDto);
        });
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

            if (response == null || topic == null) return;

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
                throw new RuntimeException(e);
            }
        });
    }
}

