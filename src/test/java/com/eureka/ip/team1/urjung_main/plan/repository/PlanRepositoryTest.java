package com.eureka.ip.team1.urjung_main.plan.repository;

import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // 추가!
public class PlanRepositoryTest {
    @Autowired
    private PlanRepository planRepository;

    // 요금제 전체 목록 조회 테스트
    @Test
    @DisplayName("Plan 저장 테스트 - PrePersist 포함")
    void savePlan() {
        // given
        Plan plan = Plan.builder()
                .name("Plan A")
                .price(40000)
                .description("Test Plan")
                .dataAmount(5000L)
                .callAmount(300L)
                .smsAmount(100L)
                .build();

        // when
        Plan savedPlan = planRepository.save(plan);

        // then
        assertThat(savedPlan.getId()).isNotNull();  // PrePersist 실행 확인
        assertThat(savedPlan.getCreatedAt()).isNotNull();
        assertThat(savedPlan.getName()).isEqualTo("Plan A");
    }

    // 요금제 상세 조회 테스트
    @Test
    @DisplayName("Plan findById 테스트 - 존재하는 Plan 조회")
    void findById_shouldReturnPlan() {
        // given
        Plan plan = Plan.builder()
                .name("Plan A")
                .price(40000)
                .description("Test Plan")
                .dataAmount(5000L)
                .callAmount(300L)
                .smsAmount(100L)
                .build();

        Plan savedPlan = planRepository.save(plan);

        // when
        Plan foundPlan = planRepository.findById(savedPlan.getId()).orElse(null);

        // then
        assertThat(foundPlan).isNotNull();
        assertThat(foundPlan.getId()).isEqualTo(savedPlan.getId());
        assertThat(foundPlan.getName()).isEqualTo(savedPlan.getName());
    }

}

