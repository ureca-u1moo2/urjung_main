package com.eureka.ip.team1.urjung_main.embedding.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import com.eureka.ip.team1.urjung_main.embedding.config.EmbeddingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingServiceImpl implements EmbeddingService {

//    private final RestTemplate restTemplate;
    @Qualifier("embeddingWebClient")
    private final WebClient webClient;
    private final ElasticsearchClient esClient;

    public Mono<Void> indexWithEmbedding(String question) {
        String docId = EmbeddingUtils.generateIdFromQuestion(question); // 고정 ID 생성

        return webClient.post()
                .uri("http://localhost:8000/embedding")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("text", question))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Float>>() {})
                .flatMap(embedding ->
                        Mono.fromCallable(() -> {
                                    esClient.index(i -> i
                                            .index("questions")
                                            .id(docId) // 고정 ID로 저장 → 중복 방지
                                            .document(Map.of(
                                                    "content", question,
                                                    "embedding", embedding
                                            ))
                                    );
                                    return true;
                                })
                                .doOnSuccess(ok -> log.info("임베딩 저장 완료: {}", question))
                                .onErrorResume(e -> {
                                    log.error("임베딩 저장 실패", e);
                                    return Mono.empty();
                                })
                                .then()
                );
    }




    public Mono<List<String>> searchSimilarQuestions(String queryText) {
        return webClient.post()
                .uri("http://localhost:8000/embedding")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("text",queryText))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Float>>() {})
                .flatMap(embedding -> {
                    try {
                        // 벡터 유사도 검색
                        var knnQuery = KnnQuery.of(k -> k
                                .field("embedding")
                                .k(1)
                                .numCandidates(30)
                                .queryVector(embedding)
                        );
                        var result = esClient.search(s -> s
                                        .index("questions")
                                        .knn(knnQuery),
                                Map.class
                        );

                        List<String> similar = result.hits().hits().stream()
                                .map(hit -> (String) hit.source().get("content"))
                                .toList();

                        return Mono.just(similar);

                    } catch (IOException e) {
                        return Mono.error(new RuntimeException("ES 유사도 검색 실패 ㅠㅠ", e));
                    }
                });
    }

    public Mono<Boolean> alreadyExists(String question) {
        String docId = EmbeddingUtils.generateIdFromQuestion(question);

        try {
            boolean exists = esClient.exists(e -> e
                    .index("questions")
                    .id(docId)
            ).value();

            return Mono.just(exists);
        } catch (IOException e) {
            return Mono.error(e);
        }
    }




}

