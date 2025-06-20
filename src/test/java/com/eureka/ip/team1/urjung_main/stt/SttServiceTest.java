package com.eureka.ip.team1.urjung_main.stt;

import com.eureka.ip.team1.urjung_main.stt.service.SttService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
// 통합 테스트 스타일로 진행
// HttpURLConnecrtion 모킹이 어려움
// 외부 호출은 테스트가 불안정한 점 고려
public class SttServiceTest {

    @Autowired
    private SttService sttService;

    @Test
    void transcribeWav() throws Exception {

        Path path = Paths.get("audio/hello.wav");
        byte[] fileContent = Files.readAllBytes(path);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "hello.wav", "audio/wav", fileContent
        );

        String transcript = sttService.transcribeWav(mockFile);

        System.out.println("Transcript: " + transcript);
        assertNotNull(transcript);
        assertFalse(transcript.isEmpty());
    }
}
