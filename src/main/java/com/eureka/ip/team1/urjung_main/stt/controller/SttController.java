package com.eureka.ip.team1.urjung_main.stt.controller;

import com.eureka.ip.team1.urjung_main.stt.service.SttService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SttController {

    private final SttService sttService;

    @PostMapping("/api/stt")
    public ResponseEntity<Map<String, String>> handleAudioUpload(@RequestParam("file") MultipartFile file) {
        String transcript = sttService.transcribeWav(file); // 내부적으로 저장 → Base64 → ETRI 호출
        return ResponseEntity.ok(Map.of("transcript", transcript));
    }
}
