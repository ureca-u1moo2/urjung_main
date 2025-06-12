package com.eureka.ip.team1.urjung_main.user.repository;

import com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Slf4j
class UsageRepositoryTest {

    @Autowired
    private UsageRepository usageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        // 1. Plan 데이터 삽입
        jdbcTemplate.update(
                "INSERT INTO plan (plan_id, plan_name, price, created_at) VALUES (?, ?, ?, ?)",
                "plan001", "기본 요금제", 50000, java.sql.Timestamp.valueOf(LocalDateTime.now())
        );

        jdbcTemplate.update(
                "INSERT INTO plan (plan_id, plan_name, price, created_at) VALUES (?, ?, ?, ?)",
                "plan002", "프리미엄 요금제", 70000, java.sql.Timestamp.valueOf(LocalDateTime.now())
        );

        // 2. Line 데이터 삽입
        jdbcTemplate.update(
                "INSERT INTO line (line_id, user_id, plan_id, phone_number, status, start_date, end_date, discounted_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                "line001", "user123", "plan001", "010-2323-2323", "active",
                java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 5, 10, 12, 0)),
                java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 10, 10, 12, 0)),
                50000
        );

        jdbcTemplate.update(
                "INSERT INTO line (line_id, user_id, plan_id, phone_number, status, start_date, end_date, discounted_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                "line002", "user123", "plan002", "010-9876-5432", "active",
                java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 5, 10, 12, 0)),
                java.sql.Timestamp.valueOf(LocalDateTime.of(2025, 10, 10, 12, 0)),
                60000
        );

        // 3. MonthlyUsage 데이터 삽입 - UUID를 바이트 배열로
        byte[] uuid1 = generateUUIDBytes();
        byte[] uuid2 = generateUUIDBytes();
        byte[] uuid3 = generateUUIDBytes();
        byte[] uuid4 = generateUUIDBytes();


        jdbcTemplate.update(
                "INSERT INTO monthly_usage (monthly_usage_id, line_id, month, data, call_minute, message) VALUES (?, ?, ?, ?, ?, ?)",
                uuid1, "line001", java.sql.Date.valueOf(LocalDate.of(2025, 6, 1)), 5000L, 300L, 100L
        );

        jdbcTemplate.update(
                "INSERT INTO monthly_usage (monthly_usage_id, line_id, month, data, call_minute, message) VALUES (?, ?, ?, ?, ?, ?)",
                uuid2, "line001", java.sql.Date.valueOf(LocalDate.of(2025, 7, 1)), 4500L, 250L, 80L
        );

        jdbcTemplate.update(
                "INSERT INTO monthly_usage (monthly_usage_id, line_id, month, data, call_minute, message) VALUES (?, ?, ?, ?, ?, ?)",
                uuid3, "line002", java.sql.Date.valueOf(LocalDate.of(2025, 6, 1)), 6000L, 400L, 150L
        );

    }

    private byte[] generateUUIDBytes() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    @Test
    void findAllUsagesByUserId_Test() {
        List<UsageResponseDto> allUsagesByUserId = usageRepository.findAllUsagesByUserId("user123");

        for(UsageResponseDto usageResponseDto : allUsagesByUserId) {
            log.info("usageResponseDto : {}", usageResponseDto);
        }

        assertEquals(3, allUsagesByUserId.size());
    }

    @Test
    void findAllUsagesByUserIdAndMonth_Test() {
        List<UsageResponseDto> allUsagesByUserIdAndMonth
                = usageRepository.findAllUsagesByUserIdAndMonth("user123", 2025, 6);

        for(UsageResponseDto usageResponseDto : allUsagesByUserIdAndMonth) {
            log.info("usageResponseDto : {}", usageResponseDto);
        }

        assertEquals(2, allUsagesByUserIdAndMonth.size());
    }

    @Test
    void findCurrentMonthUsagesByUserId_Test() {
        List<UsageResponseDto> currentMonthUsagesByUserId = usageRepository.findCurrentMonthUsagesByUserId("user123");

        for(UsageResponseDto usageResponseDto : currentMonthUsagesByUserId) {
            log.info("usageResponseDto : {}", usageResponseDto);
        }

        assertEquals(2, currentMonthUsagesByUserId.size());
    }

    @Test
    void findUsageByLineIdAndMonth() {
        Optional<UsageResponseDto> usageByLineIdAndMonth = usageRepository.findUsageByLineIdAndMonth("line001", 2025, 6);

        log.info("usageReposneDto : {}", usageByLineIdAndMonth);

        assertEquals(6, usageByLineIdAndMonth.get().getMonth());
    }

    @Test
    void findUsageByUserIdAndPlanIdAndMonth() {
        Optional<UsageResponseDto> usageByUserIdAndPlanIdAndMonth = usageRepository.findUsageByUserIdAndPlanIdAndMonth("user123", "plan002", 2025, 6);

        log.info("usageRepsonseDto : {}", usageByUserIdAndPlanIdAndMonth);

        assertEquals("plan002", usageByUserIdAndPlanIdAndMonth.get().getPlanId());
        assertEquals(6, usageByUserIdAndPlanIdAndMonth.get().getMonth());
    }
}

