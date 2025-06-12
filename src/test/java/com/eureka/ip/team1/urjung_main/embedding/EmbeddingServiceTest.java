package com.eureka.ip.team1.urjung_main.embedding;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.eureka.ip.team1.urjung_main.embedding.service.EmbeddingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmbeddingServiceTest {
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ElasticsearchClient esClient;

    @InjectMocks
    private EmbeddingServiceImpl embeddingService;

    @SuppressWarnings("unchecked") // 제네릭 타입을 명확히 지정하기 어려운 경우 사용 -> 경고가 로그에 계속 뜨는 것을 방지
    @Test
    void testIndexWithEmbedding() throws Exception {
        String question = "비싼 요금제 정보 알려줘";
        List<Float> mockEmbedding = List.of(0.1f, 0.2f, 0.3f);

        // WebClient 응답 mocking
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(mockEmbedding));

        // ElasticsearchClient index 모킹
        when(esClient.index(any(Function.class))).thenReturn(mock(IndexResponse.class));

        Mono<Void> result = embeddingService.indexWithEmbedding(question);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testSearchSimilarQuestions() throws IOException {
        String queryText = "비싼 요금제 정보 알려줘";
        List<Float> fakeEmbedding = List.of(0.1f, 0.2f, 0.3f);
        List<String> expected = List.of("A", "B", "C");

        // WebClient mock
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(fakeEmbedding));

        // Elasticsearch 응답 mock
        Hit<Map> hit1 = Hit.of(h -> h.index("questions").id("1").source(Map.of("content", "A")));
        Hit<Map> hit2 = Hit.of(h -> h.index("questions").id("2").source(Map.of("content", "B")));
        Hit<Map> hit3 = Hit.of(h -> h.index("questions").id("3").source(Map.of("content", "C")));

        SearchResponse<Map> searchResponse = SearchResponse.of(s -> s
                .took(10)
                .timedOut(false)
                .shards(ShardStatistics.of(sh -> sh
                        .total(1)
                        .successful(1)
                        .skipped(0)
                        .failed(0)
                ))
                .hits(h -> h.hits(List.of(hit1, hit2, hit3)))
        );


        when(esClient.search(any(Function.class), eq(Map.class)))
                .thenReturn(searchResponse);

        // 검증
        StepVerifier.create(embeddingService.searchSimilarQuestions(queryText))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void testAlreadyExists() throws IOException {
        String question = "비싼 요금제 정보 알려줘";

        // 검색 결과에 하나라도 있으면 true
        Hit<Map> hit = Hit.of(h -> h.index("questions").id("1").source(Map.of("content", question)));

        SearchResponse<Map> searchResponse = SearchResponse.of(s -> s
                .took(5)
                .timedOut(false)
                .shards(ShardStatistics.of(sh -> sh.total(1).successful(1).skipped(0).failed(0)))
                .hits(hits -> hits.hits(List.of(hit)))
        );

        when(esClient.search(any(Function.class), eq(Map.class))).thenReturn(searchResponse);

        StepVerifier.create(embeddingService.alreadyExists(question))
                .expectNext(true)
                .verifyComplete();
    }



}
