package com.eureka.ip.team1.urjung_main.embedding.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class PlanEmbeddingConfig {

    private final ElasticsearchClient client;

    public PlanEmbeddingConfig(ElasticsearchClient client) {
        this.client = client;
    }

    @PostConstruct
    public void createPlanIndex() throws IOException {
        boolean exists = client.indices().exists(e -> e.index("plans")).value();
        if (!exists) {
            client.indices().create(c -> c
                    .index("plans")
                    .mappings(m -> m
                            .properties("id", p -> p.keyword(k -> k))
                            .properties("name", p -> p.text(t -> t))
                            .properties("description", p -> p.text(t -> t))
                            .properties("price", p -> p.integer(i -> i))
                            .properties("call_amount", p -> p.long_(l -> l))
                            .properties("data_amount", p -> p.long_(l -> l))
                            .properties("sms_amount", p -> p.long_(l -> l))
                            .properties("embedding", p -> p.denseVector(d -> d
                                    .dims(768)
                                    .index(true)
                                    .similarity("cosine")
                            ))
                            .properties("rank_scores", p -> p
                                    .rankFeatures(rf -> rf)
                            )
                    )
            );

        }
    }



}

