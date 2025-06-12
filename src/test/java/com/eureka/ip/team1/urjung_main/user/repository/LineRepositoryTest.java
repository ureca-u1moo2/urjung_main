package com.eureka.ip.team1.urjung_main.user.repository;

import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import com.eureka.ip.team1.urjung_main.user.entity.Line;
import com.eureka.ip.team1.urjung_main.user.entity.Line.LineStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
public class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PlanRepository planRepository;

    @Test
    @DisplayName("existsByUserIdAndPlanIdAndStatus - 존재할 때 true 반환")
    void existsByUserIdAndPlanIdAndStatus_shouldReturnTrueWhenExists() {
        // given
        Plan plan = planRepository.save(Plan.builder()
                .name("Test Plan")
                .price(10000)
                .dataAmount(1000L)
                .callAmount(100L)
                .smsAmount(50L)
                .description("Test plan description")
                .build());

        String userId = "test-user";
        String planId = plan.getId();

        Line line = Line.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .planId(planId)
                .status(LineStatus.active)
                .startDate(LocalDateTime.now())
                .build();

        lineRepository.save(line);

        // when
        boolean exists = lineRepository.existsByUserIdAndPlanIdAndStatus(userId, planId, LineStatus.active);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUserIdAndPlanIdAndStatus - 존재하지 않을 때 false 반환")
    void existsByUserIdAndPlanIdAndStatus_shouldReturnFalseWhenUserNotFound() {
        // given
        Plan plan = planRepository.save(Plan.builder()
                .name("Test Plan")
                .price(20000)
                .dataAmount(2000L)
                .callAmount(200L)
                .smsAmount(100L)
                .description("Another test plan")
                .build());

        String planId = plan.getId();

        // when
        boolean exists = lineRepository.existsByUserIdAndPlanIdAndStatus("non-existent-user", planId, LineStatus.active);

        // then
        assertThat(exists).isFalse();
    }
}
