package com.eureka.ip.team1.urjung_main.embedding.service;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanDocument;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanResultDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PlanIndexingService {
    void syncPlansToElasticsearch();
    Mono<List<PlanResultDto>> searchSimilarPlans(String queryText);
}
