package com.eureka.ip.team1.urjung_main.user.repository;

import com.eureka.ip.team1.urjung_main.user.dto.UserPlanResponseDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserPlanRepositoryTest {

    @Autowired
    private UserPlanRepository userPlanRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        // 0. Membership 데이터 삽입
        jdbcTemplate.update(
                "INSERT INTO membership (id, membership_name, gift_discount, require_amount) VALUES (?, ?, ?, ?)",
                "membership001", "Basic", 10, 5000
        );

        // 1. User 데이터 삽입
        jdbcTemplate.update(
                "INSERT INTO user (user_id, name, email, password, gender, birth, membership_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                "user123", "홍길동", "test@test.com", "password", "M", "1990-01-01", "membership001"
        );

        jdbcTemplate.update(
                "INSERT INTO user (user_id, name, email, password, gender, birth, membership_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                "user456", "김철수", "test2@test.com", "password", "M", "1992-02-02", "membership001"
        );

        // 2. Plan 데이터 삽입
        jdbcTemplate.update(
                "INSERT INTO plan (plan_id, plan_name, price, description, created_at) VALUES (?, ?, ?, ?, ?)",
                "plan001", "기본 요금제", 50000, "기본 데이터 5GB, 통화 100분", java.sql.Timestamp.valueOf(LocalDateTime.now())
        );

        jdbcTemplate.update(
                "INSERT INTO plan (plan_id, plan_name, price, description, created_at) VALUES (?, ?, ?, ?, ?)",
                "plan002", "프리미엄 요금제", 70000, "데이터 무제한, 통화 무제한", java.sql.Timestamp.valueOf(LocalDateTime.now())
        );

        // 3. Line 데이터 삽입 - user123의 active 회선 2개
        jdbcTemplate.update(
                "INSERT INTO line (line_id, user_id, plan_id, phone_number, status, start_date, end_date, discounted_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                "line001", "user123", "plan001", "232-2323-2323", "active",
                java.sql.Timestamp.valueOf(LocalDateTime.of(2023, 5, 10, 12, 0)),
                java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 10, 10, 12, 0)),
                50000
        );

        jdbcTemplate.update(
                "INSERT INTO line (line_id, user_id, plan_id, phone_number, status, start_date, end_date, discounted_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                "line002", "user123", "plan002", "232-9876-5432", "active",
                java.sql.Timestamp.valueOf(LocalDateTime.of(2023, 5, 10, 12, 0)),
                java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 10, 10, 12, 0)),
                60000
        );

        // 4. Line 데이터 삽입 - user456의 active 회선 1개
        jdbcTemplate.update(
                "INSERT INTO line (line_id, user_id, plan_id, phone_number, status, start_date, end_date, discounted_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                "line003", "user456", "plan001", "232-1111-2222", "active",
                java.sql.Timestamp.valueOf(LocalDateTime.of(2023, 5, 10, 12, 0)),
                java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 10, 10, 12, 0)),
                50000
        );

        // 5. Line 데이터 삽입 - user123의 canceled 회선 1개
        jdbcTemplate.update(
                "INSERT INTO line (line_id, user_id, plan_id, phone_number, status, start_date, end_date, discounted_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                "line004", "user123", "plan001", "232-3333-4444", "canceled",
                java.sql.Timestamp.valueOf(LocalDateTime.of(2023, 1, 1, 12, 0)),
                java.sql.Timestamp.valueOf(LocalDateTime.of(2023, 4, 1, 12, 0)),
                50000
        );
    }

    @Test
    public void testFindAllPlansByUserId_성공_Test() {
        // When
        List<UserPlanResponseDto> userPlans = userPlanRepository.findAllPlansByUserId("user123");

        // Then
        assertEquals(2, userPlans.size());
        
        UserPlanResponseDto firstPlan = userPlans.stream()
                .filter(plan -> plan.getPlanId().equals("plan001"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Plan001 not found"));
        
        assertEquals("기본 요금제", firstPlan.getPlanName());
        assertEquals("232-2323-2323", firstPlan.getPhoneNumber());
        assertEquals("기본 데이터 5GB, 통화 100분", firstPlan.getDescription());
        assertEquals(50000, firstPlan.getDiscountedPrice());
        
        UserPlanResponseDto secondPlan = userPlans.stream()
                .filter(plan -> plan.getPlanId().equals("plan002"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Plan002 not found"));
        
        assertEquals("프리미엄 요금제", secondPlan.getPlanName());
        assertEquals("232-9876-5432", secondPlan.getPhoneNumber());
        assertEquals("데이터 무제한, 통화 무제한", secondPlan.getDescription());
        assertEquals(60000, secondPlan.getDiscountedPrice());
    }

    @Test
    public void testFindAllPlansByUserId_활성_요금제_리턴_Test() {
        // When
        List<UserPlanResponseDto> userPlans = userPlanRepository.findAllPlansByUserId("user123");

        // Then
        assertEquals(2, userPlans.size());
        
        for (UserPlanResponseDto plan : userPlans) {
            assertNotEquals("010-3333-4444", plan.getPhoneNumber());
        }
    }

    @Test
    public void testFindAllPlansByUserId_다른_유저_Test() {
        // When
        List<UserPlanResponseDto> userPlans = userPlanRepository.findAllPlansByUserId("user456");

        // Then
        assertEquals(1, userPlans.size());
        
        UserPlanResponseDto plan = userPlans.get(0);
        assertEquals("plan001", plan.getPlanId());
        assertEquals("기본 요금제", plan.getPlanName());
        assertEquals("232-1111-2222", plan.getPhoneNumber());
        assertEquals(50000, plan.getDiscountedPrice());
    }
}