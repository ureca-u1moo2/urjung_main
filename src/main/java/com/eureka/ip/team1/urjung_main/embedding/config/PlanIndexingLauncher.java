package com.eureka.ip.team1.urjung_main.embedding.config;

import com.eureka.ip.team1.urjung_main.embedding.service.PlanIndexingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanIndexingLauncher {
    private final PlanIndexingService planIndexingService;

    @PostConstruct
    public void init() {
        planIndexingService.syncPlansToElasticsearch();;
    }
}
