package com.eureka.ip.team1.urjung_main.plan.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GeminiApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.base-url}")
    private String baseUrl;

    @Value("${gemini.api.model-name}")
    private String modelName;

    @Value("${gemini.api.method}")
    private String method;

    public String getGeminiSummary(String title, String content) {
        String url = String.format("%s/models/%s:%s?key=%s", baseUrl, modelName, method, geminiApiKey);

        Map<String, Object> message = new HashMap<>();
        message.put("parts", List.of(Map.of("text", content)));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(Map.of("role", "user", "parts", message.get("parts"))));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> candidate = ((List<Map<String, Object>>) response.getBody().get("candidates")).get(0);
            Map<String, Object> contentMap = (Map<String, Object>) candidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
            return (String) parts.get(0).get("text");
        }

        return "AI 요약 실패. Gemini API 오류.";
    }
}
