package com.eureka.ip.team1.urjung_main.chatbot.controller;

import com.eureka.ip.team1.urjung_main.forbiddenword.service.ForbiddenWordService;
import com.eureka.ip.team1.urjung_main.forbiddenword.controller.ForbiddenWordInternalController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ForbiddenWordInternalController.class)
@AutoConfigureMockMvc(addFilters = false)
class ForbiddenWordInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ForbiddenWordService forbiddenWordService;

    @Test
    @DisplayName("금칙어 갱신 API 호출 시 200 OK 응답과 메시지를 반환해야 한다")
    void reloadForbiddenWordsTest() throws Exception {
        doNothing().when(forbiddenWordService).reloadForbiddenWords();

        mockMvc.perform(post("/internal/forbidden/reload")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("금칙어 갱신 완료"));

        Mockito.verify(forbiddenWordService).reloadForbiddenWords();
    }
}
