package com.eureka.ip.team1.urjung_main.plan.repository;

import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.user.entity.Line;
import com.eureka.ip.team1.urjung_main.user.entity.Line.LineStatus;
import com.eureka.ip.team1.urjung_main.user.repository.LineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlanRepositoryTest {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private LineRepository lineRepository; // test용 repository 따로 만들었으면 그것도 추가 필요

    @Test
    @DisplayName("가격 낮은 순 정렬 테스트")
    void sortByPriceAsc() {
        planRepository.save(Plan.builder().name("A").price(10000).build());
        planRepository.save(Plan.builder().name("B").price(20000).build());

        List<Plan> plans = planRepository.findAllByOrderByPriceAsc();
        assertThat(plans.get(0).getPrice()).isEqualTo(10000);
    }

    @Test
    @DisplayName("가격 높은 순 정렬 테스트")
    void sortByPriceDesc() {
        planRepository.save(Plan.builder().name("A").price(10000).build());
        planRepository.save(Plan.builder().name("B").price(40000).build());

        List<Plan> plans = planRepository.findAllByOrderByPriceDesc();
        assertThat(plans.get(0).getPrice()).isEqualTo(40000);
    }

    @Test
    @DisplayName("데이터 적은 순 정렬 테스트")
    void sortByDataAsc() {
        planRepository.save(Plan.builder().name("A").dataAmount(1000L).build());
        planRepository.save(Plan.builder().name("B").dataAmount(3000L).build());

        List<Plan> plans = planRepository.findAllByOrderByDataAmountAsc();
        assertThat(plans.get(0).getDataAmount()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("데이터 많은 순 정렬 테스트")
    void sortByDataDesc() {
        planRepository.save(Plan.builder().name("A").dataAmount(1000L).build());
        planRepository.save(Plan.builder().name("B").dataAmount(10000L).build());

        List<Plan> plans = planRepository.findAllByOrderByDataAmountDesc();
        assertThat(plans.get(0).getDataAmount()).isEqualTo(10000L);
    }

    @Transactional
    @Rollback
    @Test
    @DisplayName("인기순 정렬(JPQL) 테스트")
    void findPopularPlans() {
        Plan plan1 = planRepository.save(Plan.builder().name("A").build());
        Plan plan2 = planRepository.save(Plan.builder().name("B").build());

        // userId가 필수이므로 테스트용 값 넣어주기
        String fakeUser1 = "user-1";
        String fakeUser2 = "user-2";
        String fakeUser3 = "user-3";

        // 활성화된 Line 2건 (plan1)
        lineRepository.save(Line.builder()
                .planId(plan1.getId())
                .userId(fakeUser1)
                .status(LineStatus.active)
                .build());

        lineRepository.save(Line.builder()
                .planId(plan1.getId())
                .userId(fakeUser2)
                .status(LineStatus.active)
                .build());

        // 비활성화된 Line 1건 (plan2)
        lineRepository.save(Line.builder()
                .planId(plan2.getId())
                .userId(fakeUser3)
                .status(LineStatus.canceled)
                .build());

        List<Plan> popularPlans = planRepository.findPopularPlans();
        assertThat(popularPlans.get(0).getName()).isEqualTo("A");
    }
}
