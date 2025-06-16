package com.eureka.ip.team1.urjung_main.user.repository;

import com.eureka.ip.team1.urjung_main.membership.entity.Membership;
import com.eureka.ip.team1.urjung_main.membership.repository.MembershipRepository;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import com.eureka.ip.team1.urjung_main.user.entity.Line;
import com.eureka.ip.team1.urjung_main.user.entity.Line.LineStatus;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    @DisplayName("existsByUserIdAndPlanIdAndStatus - 존재할 때 true 반환")
    void existsByUserIdAndPlanIdAndStatus_shouldReturnTrueWhenExists() {
        // given
        Membership membership = new Membership();
        membership.setMembershipName("Basic");
        membership.setGiftDiscount(25.0);
        membership.setRequireAmount(1000);
        membershipRepository.save(membership);

        User user = userRepository.save(User.builder()
                .userId("test-user")
                .name("Test User")
                .email("Test@test.com")
                .password("password")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .membership(membership)
                .build());

        Plan plan = planRepository.save(Plan.builder()
                .name("Test Plan")
                .price(10000)
                .dataAmount(1000L)
                .callAmount(100L)
                .smsAmount(50L)
                .description("Test plan description")
                .build());

        String userId = user.getUserId();
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

    @Test
    @DisplayName("findAllByUserId - 특정 사용자의 회선 전체 조회")
    void findAllByUserId_shouldReturnLineList() {
        // given
        Membership membership = new Membership();
        membership.setMembershipName("Premium");
        membership.setGiftDiscount(0.2);
        membership.setRequireAmount(50000);
        membershipRepository.save(membership);

        User user = userRepository.save(User.builder()
                .userId("user999")
                .name("테스터")
                .email("user@test.com")
                .password("1234")
                .birth(LocalDate.of(1995, 5, 5))
                .gender("M")
                .membership(membership)
                .build());

        Plan plan = planRepository.save(Plan.builder()
                .name("프리미엄 요금제")
                .price(30000)
                .dataAmount(5000L)
                .callAmount(300L)
                .smsAmount(100L)
                .description("무제한 요금제")
                .build());

        Line line1 = Line.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getUserId())
                .planId(plan.getId())
                .status(Line.LineStatus.active)
                .phoneNumber("010-1000-2000")
                .discountedPrice(27000)
                .startDate(LocalDateTime.now())
                .build();

        Line line2 = Line.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getUserId())
                .planId(plan.getId())
                .status(Line.LineStatus.canceled)
                .phoneNumber("010-3000-4000")
                .discountedPrice(27000)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        lineRepository.save(line1);
        lineRepository.save(line2);

        // when
        List<Line> result = lineRepository.findAllByUserId(user.getUserId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Line::getPhoneNumber)
                .containsExactlyInAnyOrder("010-1000-2000", "010-3000-4000");
    }

}
