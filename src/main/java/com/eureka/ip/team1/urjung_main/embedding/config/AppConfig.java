package com.eureka.ip.team1.urjung_main.embedding.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// FastAPI로 구축한 임베딩 서버에 질문을 보내고 벡터 응답을 받기 위해 RestTemplate를 사용
@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

