package com.eureka.ip.team1.urjung_main.chatbot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eureka.ip.team1.urjung_main.chatbot.entity.ForbiddenWord;
import com.eureka.ip.team1.urjung_main.chatbot.repository.ForbiddenWordRepository;

@ExtendWith(MockitoExtension.class)
class ForbiddenWordServiceImplTest {

    @InjectMocks
    private ForbiddenWordServiceImpl forbiddenWordService;

    @Mock
    private ForbiddenWordRepository forbiddenWordRepository;


    @BeforeEach
    void setUp() {
        // Mock 데이터 정의
        List<ForbiddenWord> mockForbiddenWords = List.of(
                new ForbiddenWord("바보"),
                new ForbiddenWord("멍청이"),
                new ForbiddenWord("시발"),
                new ForbiddenWord("ㅅㅂ"),
                new ForbiddenWord("씨발"),
                new ForbiddenWord("병신"),
                new ForbiddenWord("개새끼")
        );

        //given
        given(forbiddenWordRepository.findAll()).willReturn(mockForbiddenWords);

        // Trie 초기화 수동
        forbiddenWordService.init();
    }

    @Test
    @DisplayName("금칙어 포함된 경우 true 반환")
    void testContainsForbiddenWordTrue() {
        assertThat(forbiddenWordService.containsForbiddenWord("야 이 병신아")).isTrue();
        assertThat(forbiddenWordService.containsForbiddenWord("이거 진짜 개새끼네")).isTrue();
    }

    @Test
    @DisplayName("금칙어가 없는 경우 gemini 호출")
    void testContainsForbiddenWordGemini() {
        assertThat(forbiddenWordService.containsForbiddenWord("안녕하세용")).isFalse();
        assertThat(forbiddenWordService.containsForbiddenWord("gemini 호출")).isFalse();
    }

    @Test
    @DisplayName("censor 메서드 - 금칙어 포함된 경우 금칙어 경고 반환")
    void testCensorForbiddenWord() {
        String result = forbiddenWordService.censor("이거 진짜 병신새끼네ㅋㅋ");
        assertThat(result).isEqualTo("입력할 수 없는 단어가 포함되어 있습니다.");
    }

    @Test
    @DisplayName("censor 메서드 - 정상 문장인 경우 '호출되었습니다.' 반환")
    void testCensorNormal() {
        String result = forbiddenWordService.censor("안녕안녕시이발~");
        assertThat(result).isEqualTo("호출되었습니다.");
    }


    @DisplayName("buildFailureLinks 에서 while 루프 강제 실행 테스트")
    @Test
    void testBuildFailureLinksWhileLoopExecuted() {
        // given
        List<ForbiddenWord> mockForbiddenWords = List.of(
                new ForbiddenWord("개병"),
                new ForbiddenWord("개병신"),
                new ForbiddenWord("병신")
        );

        BDDMockito.given(forbiddenWordRepository.findAll())
                .willReturn(mockForbiddenWords);

        ForbiddenWordServiceImpl service = new ForbiddenWordServiceImpl(forbiddenWordRepository);
        service.init();

        // when
        String input = "나는 개병으로가서 병신인 개병신을 만났따";
        boolean result = service.containsForbiddenWord(input);

        // then
        assertThat(result).isTrue();
    }


}

