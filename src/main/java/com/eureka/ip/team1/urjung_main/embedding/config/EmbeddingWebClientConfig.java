package com.eureka.ip.team1.urjung_main.embedding.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EmbeddingWebClientConfig {

    @Bean
    @Qualifier("embeddingWebClient")
    public WebClient embeddingWebClient() {
        return WebClient.builder().build();
    }
}
