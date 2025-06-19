package com.eureka.ip.team1.urjung_main.chatbot.utils;

import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Getter
public class PlanProvider {
    private final PlanService planService;
    private List<PlanDto> plans;

    @PostConstruct
    private void init() {
        this.plans = List.copyOf(planService.getPlansSorted("popular"));
    }

    public PlanDto getPlanById(String id) {
        Optional<PlanDto> optionalPlanDto = plans.stream().filter(planDto -> planDto.getId().equals(id)).findFirst();
        if (optionalPlanDto.isPresent())
            return optionalPlanDto.get();

        throw new NotFoundException("요금제가 없습니다");
    }

    public List<PlanDto> getTop5() {
        return plans.subList(0, Math.min(plans.size(), 5));
    }
}
