package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.entity.ForbiddenWord;
import com.eureka.ip.team1.urjung_main.chatbot.repository.ForbiddenWordRepository;
import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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
                createForbiddenWord("바보"),
                createForbiddenWord("멍청이"),
                createForbiddenWord("시발"),
                createForbiddenWord("ㅅㅂ"),
                createForbiddenWord("씨발"),
                createForbiddenWord("병신"),
                createForbiddenWord("개새끼")
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
                createForbiddenWord("개병"),
                createForbiddenWord("개병신"),
                createForbiddenWord("병신")
        );

        given(forbiddenWordRepository.findAll())
                .willReturn(mockForbiddenWords);

        ForbiddenWordServiceImpl service = new ForbiddenWordServiceImpl(forbiddenWordRepository);
        service.init();

        // when
        String input = "나는 개병으로가서 병신인 개병신을 만났따";
        boolean result = service.containsForbiddenWord(input);

        // then
        assertThat(result).isTrue();
    }

    // ForbiddenWord 테스트용 생성 메서드
    private ForbiddenWord createForbiddenWord(String word) {
        ForbiddenWord fw = new ForbiddenWord();
        fw.setWord(word);
        fw.setWordId(java.util.UUID.randomUUID());
        fw.setWordUpdate(java.time.LocalDateTime.now());
        fw.setWordDesc("test-desc");
        fw.setWordClass("test-class");
        return fw;
    }
}
