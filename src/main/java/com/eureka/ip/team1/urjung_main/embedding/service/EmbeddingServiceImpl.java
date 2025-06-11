package com.eureka.ip.team1.urjung_main.embedding.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

    private final RestTemplate restTemplate;
    private final ElasticsearchClient esClient;

    public void indexWithEmbedding(String question) throws IOException {
        // FastAPI에서 벡터 받기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> req = new HttpEntity<>(Map.of("text", question), headers);

        ResponseEntity<List<Float>> response = restTemplate.exchange(
                "http://localhost:8000/embedding",
                HttpMethod.POST,
                req,
                new ParameterizedTypeReference<>() {}
        );

        // ES에 저장
        esClient.index(i -> i
                .index("questions")
                .document(Map.of(
                        "content", question,
                        "embedding", response.getBody()
                ))
        );
    }

    public List<String> searchSimilarQuestions(String queryText) throws IOException {
        // FastAPI로부터 쿼리 임베딩 받기
        HttpEntity<Map<String, String>> req = new HttpEntity<>(Map.of("text", queryText), new HttpHeaders());
        ResponseEntity<List<Float>> response = restTemplate.exchange(
                "http://localhost:8000/embedding",
                HttpMethod.POST,
                req,
                new ParameterizedTypeReference<>() {}
        );

        List<Float> embedding = response.getBody();

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

        return result.hits().hits().stream()
                .map(hit -> (String) hit.source().get("content"))
                .toList();
    }

    // 추천 질문 선택 시 중복 저장 방지
    public boolean alreadyExists(String question) throws IOException{
        var result = esClient.search(s -> s
                        .index("questions")
                        .query(q -> q.term(t -> t.field("content.keyword").value(question))),
                Map.class
        );
        return !result.hits().hits().isEmpty();
    }



}

