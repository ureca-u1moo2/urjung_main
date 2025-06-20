package com.eureka.ip.team1.urjung_main.stt;

import com.eureka.ip.team1.urjung_main.stt.controller.SttController;
import com.eureka.ip.team1.urjung_main.stt.service.SttService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SttController.class)
@AutoConfigureMockMvc
class SttControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SttService sttService;

    @Test
    void handleAudioUploadTest() throws Exception {
        // given
        Path path = Paths.get("audio/hello.wav");
        byte[] fileContent = Files.readAllBytes(path);

        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.wav", "audio/wav", fileContent
        );

        Mockito.when(sttService.transcribeWav(any())).thenReturn("안녕하세요");

        // when & then
        mockMvc.perform(multipart("/api/stt").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transcript").value("안녕하세요"))
                .andDo(print());
    }
}


