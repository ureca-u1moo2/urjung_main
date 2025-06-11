package com.eureka.ip.team1.urjung_main.embedding.service;

import java.io.IOException;
import java.util.List;

public interface EmbeddingService {
    void indexWithEmbedding(String question) throws IOException;
    List<String> searchSimilarQuestions(String queryText) throws IOException;
    boolean alreadyExists(String question) throws IOException;
}
