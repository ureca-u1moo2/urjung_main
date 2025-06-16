package com.eureka.ip.team1.urjung_main.embedding.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EmbeddingUtils {
    public static String generateIdFromQuestion(String question) {
        return Base64.getUrlEncoder()
                .encodeToString(question.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
    }
}
