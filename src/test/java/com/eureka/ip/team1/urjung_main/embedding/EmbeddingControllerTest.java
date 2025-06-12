package com.eureka.ip.team1.urjung_main.embedding;


import com.eureka.ip.team1.urjung_main.embedding.controller.EmbeddingController;
import com.eureka.ip.team1.urjung_main.embedding.service.EmbeddingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@WebFluxTest(controllers = EmbeddingController.class)
@Import(EmbeddingControllerTest.TestSecurityConfig.class)
public class EmbeddingControllerTest {

    // Security 비활성
    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http.csrf().disable()
                    .authorizeExchange().anyExchange().permitAll()
                    .and().build();
        }
    }
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EmbeddingServiceImpl embeddingService;

    @Test
    @DisplayName("유사 질문 탐색")
    void searchSimilarQuestionsTest() {
        String query = "비싼 요금제 정보 알려줘";
        List<String> mockResults = List.of("프리미엄 요금제 정보 알려줘","혜택이 많은 요금제 알려줘","데이터를 많이 제공해주는 요금제 알려줘");

        Mockito.when(embeddingService.searchSimilarQuestions(query)).thenReturn(Mono.just(mockResults));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/questions/search").queryParam("q", query).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("""
            ["프리미엄 요금제 정보 알려줘",
             "혜택이 많은 요금제 알려줘",
             "데이터를 많이 제공해주는 요금제 알려줘"]
        """);

    }
}
