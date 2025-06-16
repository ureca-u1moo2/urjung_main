//package com.eureka.ip.team1.urjung_main.plan.repository;
//
//import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
//import com.eureka.ip.team1.urjung_main.user.entity.Line;
//import com.eureka.ip.team1.urjung_main.user.entity.Line.LineStatus;
//import com.eureka.ip.team1.urjung_main.user.repository.LineRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//public class PlanRepositoryTest {
//
//    @Autowired
//    private PlanRepository planRepository;
//
//    @Autowired
//    private LineRepository lineRepository; // test용 repository 따로 만들었으면 그것도 추가 필요
//
//    @Test
//    @DisplayName("가격 낮은 순 정렬 테스트")
//    void sortByPriceAsc() {
//        planRepository.save(Plan.builder().name("A").price(10000).build());
//        planRepository.save(Plan.builder().name("B").price(20000).build());
//
//        List<Plan> plans = planRepository.findAllByOrderByPriceAsc();
//        assertThat(plans.get(0).getPrice()).isEqualTo(10000);
//    }
//
//    @Test
//    @DisplayName("가격 높은 순 정렬 테스트")
//    void sortByPriceDesc() {
//        planRepository.save(Plan.builder().name("A").price(10000).build());
//        planRepository.save(Plan.builder().name("B").price(40000).build());
//
//        List<Plan> plans = planRepository.findAllByOrderByPriceDesc();
//        assertThat(plans.get(0).getPrice()).isEqualTo(40000);
//    }
//
//    @Test
//    @DisplayName("데이터 적은 순 정렬 테스트")
//    void sortByDataAsc() {
//        planRepository.save(Plan.builder().name("A").dataAmount(-1L).build());
//        planRepository.save(Plan.builder().name("B").dataAmount(3000L).build());
//
//        List<Plan> plans = planRepository.findAllByOrderByDataAmountAsc();
//        assertThat(plans.get(0).getDataAmount()).isEqualTo(-1L);
//    }
//
//    @Test
//    @DisplayName("데이터 많은 순 정렬 테스트")
//    void sortByDataDesc() {
//        planRepository.save(Plan.builder().name("A").dataAmount(1000L).build());
//        planRepository.save(Plan.builder().name("B").dataAmount(10000L).build());
//
//        List<Plan> plans = planRepository.findAllByOrderByDataAmountDesc();
//        assertThat(plans.get(0).getDataAmount()).isEqualTo(10000L);
//    }
//
//    @Transactional
//    @Rollback
//    @Test
//    @DisplayName("인기순 정렬(JPQL) 테스트")
//    void findPopularPlans() {
//        Plan plan1 = planRepository.save(Plan.builder().name("Plan A").build());
//        Plan plan2 = planRepository.save(Plan.builder().name("Plan B").build());
//
//        // userId가 필수이므로 테스트용 값 넣어주기
//        String fakeUser1 = "user-1";
//        String fakeUser2 = "user-2";
//        String fakeUser3 = "user-3";
//
//        // 활성화된 Line 2건 (plan1)
//        lineRepository.save(Line.builder()
//                .planId(plan1.getId())
//                .userId(fakeUser1)
//                .status(LineStatus.active)
//                .build());
//
//        lineRepository.save(Line.builder()
//                .planId(plan1.getId())
//                .userId(fakeUser2)
//                .status(LineStatus.active)
//                .build());
//
//        // 비활성화된 Line 1건 (plan2)
//        lineRepository.save(Line.builder()
//                .planId(plan2.getId())
//                .userId(fakeUser3)
//                .status(LineStatus.canceled)
//                .build());
//
//        List<Plan> popularPlans = planRepository.findPopularPlans();
//        assertThat(popularPlans.get(0).getName()).isEqualTo("Plan A");
//    }
//}

package com.eureka.ip.team1.urjung_main.plan.repository;

import com.eureka.ip.team1.urjung_main.membership.entity.Membership;
import com.eureka.ip.team1.urjung_main.membership.repository.MembershipRepository;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.user.entity.Line;
import com.eureka.ip.team1.urjung_main.user.entity.Line.LineStatus;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.repository.LineRepository;
import com.eureka.ip.team1.urjung_main.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional //  테스트 종료 후 자동 롤백
public class PlanRepositoryTest {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    @DisplayName("가격 낮은 순 정렬")
    void sortByPriceAsc() {
        planRepository.save(Plan.builder().name("A").price(10000).build());
        planRepository.save(Plan.builder().name("B").price(20000).build());

        List<Plan> plans = planRepository.findAllByOrderByPriceAsc();
        assertThat(plans.get(0).getPrice()).isEqualTo(10000);
    }

    @Test
    @DisplayName("가격 높은 순 정렬")
    void sortByPriceDesc() {
        planRepository.save(Plan.builder().name("A").price(10000).build());
        planRepository.save(Plan.builder().name("B").price(40000).build());

        List<Plan> plans = planRepository.findAllByOrderByPriceDesc();
        assertThat(plans.get(0).getPrice()).isEqualTo(40000);
    }

    @Test
    @DisplayName("데이터 적은 순 정렬")
    void sortByDataAsc() {
        planRepository.save(Plan.builder().name("A").dataAmount(-1L).build());
        planRepository.save(Plan.builder().name("B").dataAmount(3000L).build());

        List<Plan> plans = planRepository.findAllByOrderByDataAmountAsc();
        assertThat(plans.get(0).getDataAmount()).isEqualTo(3000L);
    }

    @Test
    @DisplayName("데이터 많은 순 정렬")
    void sortByDataDesc() {
        planRepository.save(Plan.builder().name("A").dataAmount(1000L).build());
        planRepository.save(Plan.builder().name("B").dataAmount(10000L).build());

        List<Plan> plans = planRepository.findAllByOrderByDataAmountDesc();
        assertThat(plans.get(0).getDataAmount()).isEqualTo(10000L);
    }

    @Test
    @DisplayName("인기순 정렬 - findPopularPlans() 메서드 테스트")
    void findPopularPlans() {
        //  User 선 저장
        // Membership 먼저 저장
        Membership membership = new Membership();
        membership.setMembershipName("기본멤버십");
        membership.setRequireAmount(0);
        membership.setGiftDiscount(0.1);
        membership = membershipRepository.save(membership);

        // User 생성
        User user1 = userRepository.save(User.builder()
                .name("홍길동")
                .email("hong1@example.com")
                .password("pass1")
                .gender("male")
                .birth(LocalDate.of(1990, 1, 1))
                .membership(membership)
                .build());

// User 2
        User user2 = userRepository.save(User.builder()
                .name("김영희")
                .email("kim2@example.com")
                .password("pass2")
                .gender("female")
                .birth(LocalDate.of(1992, 2, 2))
                .membership(membership)
                .build());

// User 3
        User user3 = userRepository.save(User.builder()
                .name("박철수")
                .email("park3@example.com")
                .password("pass3")
                .gender("male")
                .birth(LocalDate.of(1994, 3, 3))
                .membership(membership)
                .build());

        Plan plan1 = planRepository.save(Plan.builder().name("Plan A").build());
        Plan plan2 = planRepository.save(Plan.builder().name("Plan B").build());

        // Plan1에 활성 라인 2개 추가
        lineRepository.save(Line.builder()
                .id(UUID.randomUUID().toString())
                .planId(plan1.getId())
                .userId(user1.getUserId())
                .status(LineStatus.active)
                .build());

        lineRepository.save(Line.builder()
                .id(UUID.randomUUID().toString())
                .planId(plan1.getId())
                .userId(user2.getUserId())
                .status(LineStatus.active)
                .build());

        // Plan2에 비활성 라인 1개 추가 (카운트되지 않음)
        lineRepository.save(Line.builder()
                .id(UUID.randomUUID().toString())
                .planId(plan2.getId())
                .userId(user3.getUserId())
                .status(LineStatus.canceled)
                .build());

        // findPopularPlans는 Plan ID만 반환
        List<String> popularPlanIds = planRepository.findPopularPlans();

        // 가장 인기 있는 플랜은 Plan A여야 함 (활성 라인 2개)
        assertThat(popularPlanIds.get(0)).isEqualTo(plan1.getId());
    }

    @Test
    @DisplayName("인기순 정렬 - findByIdsWithTags() 메서드 테스트")
    void findByIdsWithTags() {
        // Membership 먼저 저장
        Membership membership = new Membership();
        membership.setMembershipName("기본멤버십");
        membership.setRequireAmount(0);
        membership.setGiftDiscount(0.1);
        membership = membershipRepository.save(membership);

        // User 생성
        User user1 = userRepository.save(User.builder()
                .name("홍길동")
                .email("hong1@example.com")
                .password("pass1")
                .gender("male")
                .birth(LocalDate.of(1990, 1, 1))
                .membership(membership)
                .build());

        User user2 = userRepository.save(User.builder()
                .name("김영희")
                .email("kim2@example.com")
                .password("pass2")
                .gender("female")
                .birth(LocalDate.of(1992, 2, 2))
                .membership(membership)
                .build());

        // Plan 생성
        Plan plan1 = planRepository.save(Plan.builder().name("Plan A").build());
        Plan plan2 = planRepository.save(Plan.builder().name("Plan B").build());

        // Plan1에 활성 라인 2개, Plan2에 활성 라인 1개 추가
        lineRepository.save(Line.builder()
                .id(UUID.randomUUID().toString())
                .planId(plan1.getId())
                .userId(user1.getUserId())
                .status(LineStatus.active)
                .build());

        lineRepository.save(Line.builder()
                .id(UUID.randomUUID().toString())
                .planId(plan1.getId())
                .userId(user2.getUserId())
                .status(LineStatus.active)
                .build());

        lineRepository.save(Line.builder()
                .id(UUID.randomUUID().toString())
                .planId(plan2.getId())
                .userId(user1.getUserId())
                .status(LineStatus.active)
                .build());

        // Plan ID 목록으로 Plan 객체들을 가져오기 (태그 포함)
        List<String> planIds = List.of(plan1.getId(), plan2.getId());
        List<Plan> plans = planRepository.findByIdsWithTags(planIds);

        // 인기순으로 정렬되어야 함 (Plan A가 먼저)
        assertThat(plans).hasSize(2);
        assertThat(plans.get(0).getName()).isEqualTo("Plan A");
        assertThat(plans.get(1).getName()).isEqualTo("Plan B");
    }
}