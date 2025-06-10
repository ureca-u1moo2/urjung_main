package com.eureka.ip.team1.urjung_main.plan;

import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForbiddenWordServiceImplTest {

    private ForbiddenWordServiceImpl forbiddenWordService;

    @BeforeEach
    void setUp() {
        forbiddenWordService = new ForbiddenWordServiceImpl();
        forbiddenWordService.init();  // @PostConstruct 직접 호출해서 초기화
    }

    @Test
    @DisplayName("금칙어가 포함되지 않은 경우 정상 메시지 반환")
    void testCensor_noForbiddenWord() {
        String text = "오늘은 좋은 하루입니다.";
        String result = forbiddenWordService.censor(text);

        assertEquals(" 호출 되었습니다.", result);
    }

    @Test
    @DisplayName("금칙어가 포함된 경우 경고 메시지 반환 - case1")
    void testCensor_containsForbiddenWord_case1() {
        String text = "너 진짜 바보야!";
        String result = forbiddenWordService.censor(text);

        assertEquals("입력할 수 없는 단어가 포함되어 있습니다.", result);
    }

    @Test
    @DisplayName("금칙어가 포함된 경우 경고 메시지 반환 - case2")
    void testCensor_containsForbiddenWord_case2() {
        String text = "이런 멍청이 같은 경우를 봤나";
        String result = forbiddenWordService.censor(text);

        assertEquals("입력할 수 없는 단어가 포함되어 있습니다.", result);
    }

    @Test
    @DisplayName("금칙어가 여러 번 포함된 경우도 정상 탐지")
    void testCensor_multipleForbiddenWords() {
        String text = "시발 멍청이야 개새끼야";
        String result = forbiddenWordService.censor(text);

        assertEquals("입력할 수 없는 단어가 포함되어 있습니다.", result);
    }

    @Test
    @DisplayName("빈 문자열 테스트")
    void testCensor_emptyString() {
        String text = "";
        String result = forbiddenWordService.censor(text);

        assertEquals(" 호출 되었습니다.", result);
    }
}
