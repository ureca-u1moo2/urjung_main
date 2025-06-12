package com.eureka.ip.team1.urjung_main.chatbot.controller;

import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/forbidden")
public class ForbiddenWordInternalController {

    private final ForbiddenWordService forbiddenWordService;

    public ForbiddenWordInternalController(ForbiddenWordService forbiddenWordService) {
        this.forbiddenWordService = forbiddenWordService;
    }

    @PostMapping("/reload")
    public ResponseEntity <String> reload() {
        forbiddenWordService.reloadForbiddenWords();
        return ResponseEntity.ok().body("금칙어 갱신 완료");
    }

}
