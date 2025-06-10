package com.eureka.ip.team1.urjung_main.log.controller;

import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.log.dto.ChatLogDto;
import com.eureka.ip.team1.urjung_main.log.service.ElasticsearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
public class ChatLogController {

    private final ElasticsearchLogService logService;

//    @PostMapping("/dummy")
//    public ResponseEntity<String> saveDummyLog() throws IOException {
//        ChatLogDto dummy = new ChatLogDto(
//                "user110",
//                "session-001",
//                Instant.now(),
//                "상담사랑 연결하고 싶어요",
//                Topic.INFO,
//                "U+데이터ON 요금제를 추천드립니다."
//                List.of("U+데이터ON", "5G 슬림", "5G 프리미엄"),
//                "5G 프리미엄",
//                200L
//        );
//
//        logService.saveChatLog(dummy);
//        return ResponseEntity.ok("야호 저장 완료!");
//    }
}