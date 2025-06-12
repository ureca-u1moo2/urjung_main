package com.eureka.ip.team1.urjung_main.chatbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${gemini.api.base-url}")
    private String baseUrl;
    @Value("${gemini.api.model-name}")
    private String modelName;
    @Value("${gemini.api.method}")
    private String method;

    @Bean
    @Primary // FastAPI도 WebClient를 사용하는데 WebClient Bean이 2개라 Gemini WebClient를 메인으로 설정
    public WebClient geminiWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public String geminiFullPath() {
        return "/models/" + modelName + ":" + method;
    }
}
