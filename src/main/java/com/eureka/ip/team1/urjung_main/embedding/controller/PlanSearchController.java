package com.eureka.ip.team1.urjung_main.embedding.controller;

import com.eureka.ip.team1.urjung_main.embedding.service.PlanIndexingService;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plans/search")
@RequiredArgsConstructor
public class PlanSearchController {

    private final PlanIndexingService planIndexingService;

    // POST 요청으로 질문 텍스트를 받아 유사한 요금제 설명 리스트 반환
    @PostMapping
    public ResponseEntity<List<PlanResultDto>> searchSimilarPlans(@RequestBody String queryText) {
        return planIndexingService.searchSimilarPlans(queryText)
                .map(ResponseEntity::ok)
                .blockOptional()
                .orElse(ResponseEntity.internalServerError().build());
    }
}
