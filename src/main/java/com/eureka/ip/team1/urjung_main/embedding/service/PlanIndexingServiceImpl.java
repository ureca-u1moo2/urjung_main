package com.eureka.ip.team1.urjung_main.embedding.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScore;
import com.eureka.ip.team1.urjung_main.embedding.config.PlanEmbeddingFormatter;
import com.eureka.ip.team1.urjung_main.embedding.enums.RankScoreType;
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
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


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
            String text = PlanEmbeddingFormatter.formatForEmbedding(
                    plan.getName(),
                    plan.getPrice(),
                    plan.getDataAmount(),
                    plan.getCallAmount(),
                    plan.getSmsAmount(),
                    plan.getDescription()
            );

            Map<String, Float> rankScores = generateRankScores(plan);

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
                                .rankScores(rankScores)
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
                                .numCandidates(30)
                                .queryVector(embedding)
                        );

                        List<FunctionScore> dynamicFunctions = getDynamicRankFunctions(queryText);

                        var result = esClient.search(s -> s
                                        .index("plans")
                                        .knn(knnQuery)
                                        .size(5)
                                        .query(q -> q
                                                .functionScore(fs -> fs
                                                        .query(inner -> inner.bool(b -> b
                                                                .should(m -> m.match(mm -> mm
                                                                        .field("description")
                                                                        .query(queryText)
                                                                        .boost(2.0f)
                                                                ))
                                                                .should(m -> m.match(mm -> mm
                                                                        .field("name")
                                                                        .query(queryText)
                                                                        .boost(2.0f)
                                                                ))
                                                                .minimumShouldMatch("1")
                                                        ))
                                                        .functions(dynamicFunctions)
                                                        .boostMode(FunctionBoostMode.Multiply)
                                                )
                                        ),
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

    public Map<String, Float> generateRankScores(Plan plan) {
        EnumMap<RankScoreType, Float> scoreMap = new EnumMap<>(RankScoreType.class);

        if (plan.getDataAmount() >= 20480) {
            scoreMap.put(RankScoreType.DATA_HEAVY, 1.0f);
        } else if (plan.getDataAmount() >= 10240) {
            scoreMap.put(RankScoreType.DATA_HEAVY, 0.8f);
        }

        if (plan.getCallAmount() >= 99999) {
            scoreMap.put(RankScoreType.CALL_HEAVY, 1.0f);
        }

        if (plan.getPrice() <= 15000) {
            scoreMap.put(RankScoreType.LOW_PRICE, 1.0f);
        } else if (plan.getPrice() <= 25000) {
            scoreMap.put(RankScoreType.LOW_PRICE, 0.8f);
        }

        if (plan.getCallAmount() >= 300 && plan.getPrice() <= 25000) {
            scoreMap.put(RankScoreType.SENIOR_FRIENDLY, 1.0f);
        }

        String lowerName = plan.getName().toLowerCase();
        String lowerDesc = plan.getDescription() == null ? "" : plan.getDescription().toLowerCase();

        if (lowerName.contains("청소년") || lowerDesc.contains("청소년") || lowerName.contains("학생")) {
            scoreMap.put(RankScoreType.STUDENT_FRIENDLY, 1.0f);
        }

        if (lowerName.contains("유튜브") || lowerDesc.contains("영상") || plan.getDataAmount() >= 30000) {
            scoreMap.put(RankScoreType.STREAMING, 1.0f);
        }

        if (lowerName.contains("해외") || lowerName.contains("로밍") || lowerDesc.contains("로밍")) {
            scoreMap.put(RankScoreType.ROAMING, 1.0f);
        }

        return scoreMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().field(),
                        Map.Entry::getValue
                ));
    }



    public List<FunctionScore> getDynamicRankFunctions(String queryText) {
        List<FunctionScore> functions = new ArrayList<>();
        String lower = queryText.toLowerCase();

        Map<Predicate<String>, RankScoreType> keywordMap = Map.of(
                q -> q.contains("어르신") || q.contains("시니어") || q.contains("노인"), RankScoreType.SENIOR_FRIENDLY,
                q -> q.contains("학생") || q.contains("청소년") || q.contains("미성년자"), RankScoreType.STUDENT_FRIENDLY,
                q -> q.contains("영상") || q.contains("스트리밍") || q.contains("유튜브"), RankScoreType.DATA_HEAVY,
                q -> q.contains("저렴") || q.contains("가격") || q.contains("알뜰") || q.contains("싼"), RankScoreType.LOW_PRICE
        );

        keywordMap.forEach((predicate, type) -> {
            if (predicate.test(lower)) {
                functions.add(FunctionScore.of(f -> f
                        .fieldValueFactor(ff -> ff
                                .field(type.field())
                                .factor(2.0)
                                .modifier(FieldValueFactorModifier.Sqrt)
                                .missing(0.0)
                        )
                ));
            }
        });

        return functions;
    }

}
