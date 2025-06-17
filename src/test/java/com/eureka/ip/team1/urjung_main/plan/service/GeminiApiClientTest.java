package com.eureka.ip.team1.urjung_main.plan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class GeminiApiClientTest {

    @Mock
    private RestTemplate mockRestTemplate;

    @InjectMocks
    private GeminiApiClient geminiApiClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 필드 주입 (Reflection)
        ReflectionTestUtils.setField(geminiApiClient, "geminiApiKey", "dummyKey");
        ReflectionTestUtils.setField(geminiApiClient, "baseUrl", "https://dummy.api");
        ReflectionTestUtils.setField(geminiApiClient, "modelName", "dummy-model");
        ReflectionTestUtils.setField(geminiApiClient, "method", "dummyMethod");

        // RestTemplate 교체 (기존 final → @InjectMocks로 덮음)
        ReflectionTestUtils.setField(geminiApiClient, "restTemplate", mockRestTemplate);
    }

    @Test
    @DisplayName("Gemini 요약 요청 성공 시 텍스트 반환")
    void getGeminiSummary_success() {
        // given
        String title = "요금제 제목";
        String content = "요금제 내용";

        Map<String, Object> responseBody = Map.of(
                "candidates", List.of(
                        Map.of("content", Map.of(
                                "parts", List.of(Map.of("text", "요약된 텍스트"))
                        ))
                )
        );

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(mockRestTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        // when
        String result = geminiApiClient.getGeminiSummary(title, content);

        // then
        assertThat(result).isEqualTo("요약된 텍스트");
    }

    @Test
    @DisplayName("Gemini 응답 실패 시 기본 오류 메시지 반환")
    void getGeminiSummary_failure_dueToHttpError() {
        // given
        ResponseEntity<Map> mockErrorResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        when(mockRestTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockErrorResponse);

        // when
        String result = geminiApiClient.getGeminiSummary("제목", "내용");

        // then
        assertThat(result).isEqualTo("AI 요약 실패. Gemini API 오류.");
    }

    @Test
    @DisplayName("Gemini 응답이 200이어도 body가 null인 경우 실패 메시지 반환")
    void getGeminiSummary_failure_dueToNullBody() {
        // given
        when(mockRestTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // when
        String result = geminiApiClient.getGeminiSummary("제목", "내용");

        // then
        assertThat(result).isEqualTo("AI 요약 실패. Gemini API 오류.");
    }
}
