package com.eureka.ip.team1.urjung_main.plan.repository;

import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

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

    // 요금제 필터 조건 조회 테스트
    @Test
    @DisplayName("Plan 정렬 테스트 - 가격 오름차순")
    void findAllByOrderByPriceAsc_shouldReturnSortedPlans() {
        planRepository.deleteAll();
        // given
        Plan plan1 = Plan.builder().name("Plan A").price(30000).description("Desc A").dataAmount(1000L).callAmount(100L).smsAmount(50L).build();
        Plan plan2 = Plan.builder().name("Plan B").price(50000).description("Desc B").dataAmount(2000L).callAmount(200L).smsAmount(100L).build();
        Plan plan3 = Plan.builder().name("Plan C").price(40000).description("Desc C").dataAmount(1500L).callAmount(150L).smsAmount(75L).build();

        planRepository.save(plan1);
        planRepository.save(plan2);
        planRepository.save(plan3);
        planRepository.flush();

        // when
        List<Plan> result = planRepository.findAllByOrderByPriceAsc();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getPrice()).isEqualTo(30000);
        assertThat(result.get(1).getPrice()).isEqualTo(40000);
        assertThat(result.get(2).getPrice()).isEqualTo(50000);
    }

    @Test
    @DisplayName("Plan 정렬 테스트 - 가격 내림차순")
    void findAllByOrderByPriceDesc_shouldReturnSortedPlans() {
        planRepository.deleteAll();

        // given
        Plan plan1 = Plan.builder().name("Plan A").price(30000).description("Desc A").dataAmount(1000L).callAmount(100L).smsAmount(50L).build();
        Plan plan2 = Plan.builder().name("Plan B").price(50000).description("Desc B").dataAmount(2000L).callAmount(200L).smsAmount(100L).build();
        Plan plan3 = Plan.builder().name("Plan C").price(40000).description("Desc C").dataAmount(1500L).callAmount(150L).smsAmount(75L).build();

        planRepository.save(plan1);
        planRepository.save(plan2);
        planRepository.save(plan3);
        planRepository.flush();

        // when
        List<Plan> result = planRepository.findAllByOrderByPriceDesc();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getPrice()).isEqualTo(50000);
        assertThat(result.get(1).getPrice()).isEqualTo(40000);
        assertThat(result.get(2).getPrice()).isEqualTo(30000);
    }

    @Test
    @DisplayName("Plan 정렬 테스트 - 데이터 적은 순 (오름차순)")
    void findAllByOrderByDataAmountAsc_shouldReturnSortedPlans() {
        planRepository.deleteAll();

        // given
        Plan plan1 = Plan.builder().name("Plan A").price(30000).description("Desc A").dataAmount(1000L).callAmount(100L).smsAmount(50L).build();
        Plan plan2 = Plan.builder().name("Plan B").price(50000).description("Desc B").dataAmount(2000L).callAmount(200L).smsAmount(100L).build();
        Plan plan3 = Plan.builder().name("Plan C").price(40000).description("Desc C").dataAmount(1500L).callAmount(150L).smsAmount(75L).build();

        planRepository.save(plan1);
        planRepository.save(plan2);
        planRepository.save(plan3);
        planRepository.flush();

        // when
        List<Plan> result = planRepository.findAllByOrderByDataAmountAsc();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getDataAmount()).isEqualTo(1000L);
        assertThat(result.get(1).getDataAmount()).isEqualTo(1500L);
        assertThat(result.get(2).getDataAmount()).isEqualTo(2000L);
    }

    @Test
    @DisplayName("Plan 정렬 테스트 - 데이터 많은 순")
    void findAllByOrderByDataAmountDesc_shouldReturnSortedPlans() {
        planRepository.deleteAll();
        // given
        Plan plan1 = Plan.builder().name("Plan A").price(30000).description("Desc A").dataAmount(1000L).callAmount(100L).smsAmount(50L).build();
        Plan plan2 = Plan.builder().name("Plan B").price(50000).description("Desc B").dataAmount(2000L).callAmount(200L).smsAmount(100L).build();
        Plan plan3 = Plan.builder().name("Plan C").price(40000).description("Desc C").dataAmount(1500L).callAmount(150L).smsAmount(75L).build();

        planRepository.save(plan1);
        planRepository.save(plan2);
        planRepository.save(plan3);
        planRepository.flush();

        // when
        List<Plan> result = planRepository.findAllByOrderByDataAmountDesc();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getDataAmount()).isEqualTo(2000L);
        assertThat(result.get(1).getDataAmount()).isEqualTo(1500L);
        assertThat(result.get(2).getDataAmount()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("Plan 인기순 조회 테스트 - findPopularPlans (단순 실행 확인)")
    void findPopularPlans_shouldExecute() {
        // given
        Plan plan1 = Plan.builder().name("Plan A").price(30000).description("Desc A").dataAmount(1000L).callAmount(100L).smsAmount(50L).build();
        Plan plan2 = Plan.builder().name("Plan B").price(50000).description("Desc B").dataAmount(2000L).callAmount(200L).smsAmount(100L).build();

        planRepository.save(plan1);
        planRepository.save(plan2);

        // when
        List<Plan> result = planRepository.findPopularPlans();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();  // 현재 라인 테이블 연결은 안되어 있지만 쿼리가 오류 없이 실행되는지 확인
    }

}

