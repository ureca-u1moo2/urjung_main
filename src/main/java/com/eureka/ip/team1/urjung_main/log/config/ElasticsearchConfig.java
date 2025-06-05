package com.eureka.ip.team1.urjung_main.log.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {
    // 보안 비활성화 상태
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // RestClient는 Elasticsearch에 HTTP로 요청을 보내는 저수준 클라이언트 (Elasticsearch 서버에 연결)
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http"))
                .build();

        // JavaTimeModule 등록된 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO 형식 저장

        // RestClientTransport는 고수준 클라이언트인 ElasticsearchClient와 저수준 RestClient 사이의 데이터 전송 중계 역할
        // JacksonJsonpMapper는 Java 객체 <-> JSON 간 자동 직렬화/역직렬화 처리 도구
        // DTO를 JSON으로 자동 변환
        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(objectMapper)
        );
        // 최종적으로 Elasticsearch용 고수준 클라이언트 객체 생성
        // ElasticsearchClient를 이용해 .index(), .search() 등 API를 편리하게 호출 가능
        return new ElasticsearchClient(transport);
    }
}
