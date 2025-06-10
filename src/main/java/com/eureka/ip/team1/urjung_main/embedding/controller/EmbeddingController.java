package com.eureka.ip.team1.urjung_main.embedding.controller;

import com.eureka.ip.team1.urjung_main.embedding.service.EmbeddingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class EmbeddingController {

    private final EmbeddingServiceImpl embeddingServiceImpl;

    @PostMapping("/add")
    public ResponseEntity<?> addQuestion(@RequestParam String text) throws IOException {
        embeddingServiceImpl.indexWithEmbedding(text);
        return ResponseEntity.ok("Indexed");
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> search(@RequestParam String q) throws IOException {
        return ResponseEntity.ok(embeddingServiceImpl.searchSimilarQuestions(q));
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<?> addBulkQuestions() throws IOException {
        List<String> dummyQuestions = List.of(
//            "4인 가족 요금제 추천해줘",
//            "1인 가구 요금제 추천해줘",
//            "프리미엄 요금제 알려줘",
                "어르신 요금제 뭐가 있어?",
                "청소년 요금제 추천 가능?",
//            "부모님이랑 같이 쓰는 요금제 있어?",
                "가성비 요금제 뭐 있어?",
//            "무제한 데이터 요금제 추천",
//            "통화 많이 하는 사람 요금제 뭐가 좋아?",
                "인터넷 결합 요금제 알려줘",
                "넷플릭스 포함된 요금제 있어?",
                "5G 요금제 추천해줘",
                "알뜰폰 요금제 괜찮은 거 있을까?",
                "요금제 비교 좀 해줘",
//            "가장 인기 있는 요금제는?",
                "30GB 이상 데이터 제공하는 요금제 추천해줘",
                "자주 해외 가는 사람한테 좋은 요금제 있어?",
//            "학생 요금제 어떤 게 있어?",
                "게임 많이 하는 사람한테 좋은 요금제 뭐야?",
                "요즘 가장 핫한 요금제는 뭐야?",
                "통신사 포인트 많이 주는 요금제 뭐 있어?",
                "통화+문자 무제한 요금제 알려줘",
                "와이파이 무료 제공 요금제 있을까?",
                "LTE 요금제 중 저렴한 거 알려줘",
                "중년층 요금제 뭐가 나을까?",
                "영상 많이 보는 사람 요금제 뭐야?",
                "요금제 상담 받고 싶어",
                "현재 내 요금제보다 좋은 거 있어?",
                "가족 결합하면 할인되는 요금제는?",
                "통화 음질 좋은 요금제 추천해줘"
        );
        embeddingServiceImpl.bulkIndexQuestions(dummyQuestions);
        return ResponseEntity.ok("저장 완료");
    }

}

