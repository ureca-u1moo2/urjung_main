package com.eureka.ip.team1.urjung_main.chatbot.controller;

import com.eureka.ip.team1.urjung_main.chatbot.dto.FilterRequest;
import com.eureka.ip.team1.urjung_main.chatbot.dto.FilterResponse;
import com.eureka.ip.team1.urjung_main.chatbot.service.ForbiddenWordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filter")  // 관리자가 금칙어 관리시 갱신되는 컨트롤러 입니다
public class ForbiddenWordController {

    private final ForbiddenWordService forbiddenWordService;

    public ForbiddenWordController(ForbiddenWordService forbiddenWordService) {
        this.forbiddenWordService = forbiddenWordService;
    }

    @PostMapping
    public ResponseEntity<FilterResponse> filter(@RequestBody FilterRequest filterRequest) {
        String result = forbiddenWordService.censor(filterRequest.getText());
        return ResponseEntity.ok(new FilterResponse(result));
    }
}
