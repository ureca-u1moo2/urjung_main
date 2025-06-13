package com.eureka.ip.team1.urjung_main.embedding.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanDocument;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanResultDto;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface PlanIndexingService {
    void syncPlansToElasticsearch();
    Mono<List<PlanResultDto>> searchSimilarPlans(String queryText);
    Map<String, Float> generateRankScores(Plan plan);
}
