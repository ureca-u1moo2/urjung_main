package com.eureka.ip.team1.urjung_main.chatbot.controller;

import com.eureka.ip.team1.urjung_main.chatbot.controller.ForbiddenWordController;
import com.eureka.ip.team1.urjung_main.chatbot.dto.FilterRequest;
import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ForbiddenWordController.class)
public class ForbiddenWordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ForbiddenWordService forbiddenWordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser  // WithMockUser 추가
    @DisplayName("금칙어 포함된 경우 필터링 응답 확인")
    void testFilterForbiddenWord() throws Exception {
        //given
        String inputText = "병신ㅋㅋ";
        String mockResult = "입력할 수 없는 단어가 포함되어 있습니다.";

        BDDMockito.given(forbiddenWordService.censor(anyString()))
                .willReturn(mockResult);

        FilterRequest request = new FilterRequest();
        request.setText(inputText);

        mockMvc.perform(post("/filter")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()) // CSRF 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(mockResult));
    }

    @Test
    @WithMockUser    // WithMockUser 추가
    @DisplayName("정상 문장인 경우 호출됨 응답 확인")
    void testFilterNormalText() throws Exception {
        // given
        String inputText = "오늘 날씨가 좋아요";
        String mockResult = "호출되었습니다.";

        BDDMockito.given(forbiddenWordService.censor(anyString()))
                .willReturn(mockResult);

        FilterRequest request = new FilterRequest();
        request.setText(inputText);

        // when + then
        mockMvc.perform(post("/filter")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()) // CSRF 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(mockResult));
    }
}
