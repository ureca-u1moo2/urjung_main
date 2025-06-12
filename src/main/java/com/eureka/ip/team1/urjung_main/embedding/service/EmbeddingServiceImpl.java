package com.eureka.ip.team1.urjung_main.embedding.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import lombok.RequiredArgsConstructor;
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
public class EmbeddingServiceImpl implements EmbeddingService {

//    private final RestTemplate restTemplate;
    @Qualifier("embeddingWebClient")
    private final WebClient webClient;
    private final ElasticsearchClient esClient;

    public Mono<Void> indexWithEmbedding(String question) {
        return webClient.post()
                .uri("http://localhost:8000/embedding")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("text", question))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Float>>() {})
                .flatMap(embedding -> {
                    try {
                        esClient.index(i -> i
                                .index("questions")
                                .document(Map.of(
                                        "content", question,
                                        "embedding", embedding
                                ))
                        );
                        return Mono.empty(); // 저장 후 완료 반환
                    } catch (IOException e) {
                        return Mono.error(new RuntimeException("ES 저장 실패 ㅠㅠ", e));
                    }
                });
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
                                .k(3)
                                .numCandidates(10)
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
        try {
            var result = esClient.search(s -> s
                            .index("questions")
                            .query(q -> q.term(t -> t.field("content.keyword").value(question))),
                    Map.class
            );
            return Mono.just(!result.hits().hits().isEmpty());
        } catch (IOException e) {
            return Mono.error(e);
        }
    }



}

