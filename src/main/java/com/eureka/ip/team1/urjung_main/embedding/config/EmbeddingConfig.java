package com.eureka.ip.team1.urjung_main.embedding.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class EmbeddingConfig {

    private final ElasticsearchClient client;

    public EmbeddingConfig(ElasticsearchClient client) {
        this.client = client;
    }

    // app 시작 시 한 번 실행되는데 ES에 question index가 없으면 생성
    // create는 매핑 정의
    // denseVector: ES에서 벡터 임베딩을 저장하기 위한 필드 타입. 검색 시 벡터 유사도 기반 검색 가능
    @PostConstruct
    public void initIndex() throws IOException {
        boolean exists = client.indices().exists(b -> b.index("questions")).value();

        if (!exists) {
            client.indices().create(c -> c
                    .index("questions")
                    .mappings(m -> m
                            .properties("content", p -> p
                                    .text(t -> t
                                            .fields("keyword", ft -> ft.keyword(k -> k))
                                    )
                            )
                            .properties("embedding", p -> p
                                    .denseVector(d -> d
                                            .dims(768)
                                            .index(true)
                                            .similarity("cosine")
                                    )
                            )
                    )
            );
        }
    }

}
