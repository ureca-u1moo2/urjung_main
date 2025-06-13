package com.eureka.ip.team1.urjung_main.embedding.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDocument;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanResultDto;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PlanIndexingServiceImpl implements PlanIndexingService {

    private final PlanRepository planRepository;
    private final ElasticsearchClient esClient;

    @Qualifier("embeddingWebClient")
    private final WebClient webClient;


    public void syncPlansToElasticsearch() {
        List<Plan> plans = planRepository.findAll();

        for (Plan plan : plans) {
            String text = plan.getName() + " - " + plan.getDescription();

            webClient.post()
                    .uri("http://localhost:8000/embedding")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("text", text))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Float>>() {})
                    .flatMap(embedding -> {
                        PlanDocument document = PlanDocument.builder()
                                .id(plan.getId())
                                .name(plan.getName())
                                .description(plan.getDescription())
                                .price(plan.getPrice())
                                .call_amount(plan.getCallAmount())
                                .data_amount(plan.getDataAmount())
                                .sms_amount(plan.getSmsAmount())
                                .embedding(embedding)
                                .build();

                        return Mono.fromCallable(() -> { // 동기 실행
                                    boolean exists = esClient.exists(e -> e
                                            .index("plans")
                                            .id(document.getId())
                                    ).value();

                                    if (!exists) {
                                        esClient.index(i -> i
                                                .index("plans")
                                                .id(document.getId())
                                                .document(document)
                                        );
                                    }

                                    return true;
                                })
                                .onErrorResume(e -> {
                                    System.err.println("Elasticsearch 저장 실패: " + e.getMessage());
                                    return Mono.empty();
                                });
                    })
                    .subscribe();
        }
    }

    public Mono<List<PlanResultDto>> searchSimilarPlans(String queryText) {
        return webClient.post()
                .uri("http://localhost:8000/embedding")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("text", queryText))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Float>>() {})
                .flatMap(embedding -> {
                    try {
                        var knnQuery = KnnQuery.of(k -> k
                                .field("embedding")
                                .k(5)
                                .numCandidates(50)
                                .queryVector(embedding)
                        );

                        var result = esClient.search(s -> s
                                        .index("plans")
                                        .knn(knnQuery),
                                PlanDocument.class
                        );

                        List<PlanResultDto> recommended = result.hits().hits().stream()
                                .map(hit -> {
                                    PlanDocument doc = hit.source();
                                    return PlanResultDto.builder()
                                            .id(doc.getId())
                                            .name(doc.getName())
                                            .description(doc.getDescription())
                                            .price(doc.getPrice())
                                            .callAmount(doc.getCall_amount())
                                            .dataAmount(doc.getData_amount())
                                            .smsAmount(doc.getSms_amount())
                                            .score(hit.score())
                                            .build();
                                })
                                .toList();

                        return Mono.just(recommended);
                    } catch (IOException e) {
                        return Mono.error(new RuntimeException("요금제 벡터 검색 실패", e));
                    }
                });
    }


}
