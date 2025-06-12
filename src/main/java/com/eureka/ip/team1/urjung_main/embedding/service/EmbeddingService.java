package com.eureka.ip.team1.urjung_main.embedding.service;

import reactor.core.publisher.Mono;

import java.util.List;

public interface EmbeddingService {
    Mono<Void> indexWithEmbedding(String question);
    Mono<List<String>> searchSimilarQuestions(String queryText);
    Mono<Boolean> alreadyExists(String question);
}
