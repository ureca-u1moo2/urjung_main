// GeminiApiClientTest.java
package com.eureka.ip.team1.urjung_main.plan.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class GeminiApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GeminiApiClient geminiApiClient = new GeminiApiClient();

    public GeminiApiClientTest() {
        MockitoAnnotations.openMocks(this);
        geminiApiClient = new GeminiApiClient();
    }

    @Test
    @DisplayName("Gemini 요약 성공")
    void testGeminiSummarySuccess() {
        // Given
        String title = "Test Plan";
        String content = "이 요금제는 정말 좋습니다.";

        String mockSummary = "요약된 리뷰 내용입니다.";
        Map<String, Object> mockResponse = Map.of(
                "candidates", List.of(
                        Map.of("content", Map.of("parts", List.of(Map.of("text", mockSummary))))
                )
        );

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        // Mock RestTemplate
        GeminiApiClient client = new GeminiApiClient() {
            @Override
            public String getGeminiSummary(String title, String content) {
                return mockSummary;
            }
        };

        // Then
        String result = client.getGeminiSummary(title, content);
        assertThat(result).isEqualTo(mockSummary);
    }

    @Test
    @DisplayName("Gemini 요약 실패")
    void testGeminiSummaryFailure() {
        GeminiApiClient client = new GeminiApiClient() {
            @Override
            public String getGeminiSummary(String title, String content) {
                return "AI 요약 실패. Gemini API 오류.";
            }
        };

        String result = client.getGeminiSummary("title", "content");
        assertThat(result).isEqualTo("AI 요약 실패. Gemini API 오류.");
    }
}
