package com.eureka.ip.team1.urjung_main.user.repository;

import com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto;
import com.eureka.ip.team1.urjung_main.user.entity.MonthlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsageRepository extends JpaRepository<MonthlyUsage, UUID> {

    // 특정 유저의 모든 월별 사용량
    @Query(
            """
            select new com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto(
                l.planId,
                l.phoneNumber,
                extract(year from m.month),
                extract(month from m.month),
                m.data,
                m.callMinute,
                m.message
            )
            from MonthlyUsage m join m.line l
            where l.userId = :userId
            order by m.month desc, l.planId asc
            """
    )
    List<UsageResponseDto> findAllUsagesByUserId(
            @Param("userId") String userId
    );

    // 특정 유저의 특정 월 사용량
    @Query(
            """
            select new com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto(
                l.planId,
                l.phoneNumber,
                extract(year from m.month),
                extract(month from m.month),
                m.data,
                m.callMinute,
                m.message
            )
            from MonthlyUsage m join m.line l
            where l.userId = :userId
            and extract(year from m.month) = :year
            and extract(month from m.month) = :month
            order by m.month desc, l.planId asc
            """
    )
    List<UsageResponseDto> findAllUsagesByUserIdAndMonth(
            @Param("userId") String userId,
            @Param("year") int year,
            @Param("month") int month
    );

    // 특정 유저의 금월 사용량
    @Query(
            """
            select new com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto(
                l.planId,
                l.phoneNumber,
                extract(year from m.month),
                extract(month from m.month),
                m.data,
                m.callMinute,
                m.message
            )
            from MonthlyUsage m join m.line l
            where l.userId = :userId
            and extract(year from m.month) = extract(year from current_date)
            and extract(month from m.month) = extract(month from current_date)
            order by l.planId asc
            """
    )
    List<UsageResponseDto> findCurrentMonthUsagesByUserId(
            @Param("userId") String userId
    );

    // 특정 유저의 특정 요금제 특정 월 사용량
    @Query(
            """
            select new com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto(
                l.planId,
                l.phoneNumber,
                extract(year from m.month),
                extract(month from m.month),
                m.data,
                m.callMinute,
                m.message
            )
            from MonthlyUsage m join m.line l
            where l.id = :lineId
            and extract(year from m.month) = :year
            and extract(month from m.month) = :month
            """
    )
    Optional<UsageResponseDto> findUsageByLineIdAndMonth(
            @Param("lineId") String lineId,
            @Param("year") int year,
            @Param("month") int month
    );

    @Query(
            """
            select new com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto(
                l.planId,
                l.phoneNumber,
                extract(year from m.month),
                extract(month from m.month),
                m.data,
                m.callMinute,
                m.message
            )
            from MonthlyUsage m join m.line l
            where l.userId = :userId
            and l.planId = :planId
            and extract(year from m.month) = :year
            and extract(month from m.month) = :month
            """
    )
    Optional<UsageResponseDto> findUsageByUserIdAndPlanIdAndMonth(
            @Param("userId") String userId,
            @Param("planId") String planId,
            @Param("year") int year,
            @Param("month") int month
    );

    @Query(
            """
            select new com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto(
                l.planId,
                l.phoneNumber,
                extract(year from m.month),
                extract(month from m.month),
                m.data,
                m.callMinute,
                m.message
            )
            from MonthlyUsage m join m.line l
            where l.userId = :userId
            and l.phoneNumber = :phoneNumber
            and extract(year from m.month) = extract(year from current_date)
            and extract(month from m.month) = extract(month from current_date)
            """
    )
    Optional<UsageResponseDto> findCurrentMonthUsageByUserIdAndPhoneNumber(
            @Param("userId") String userId,
            @Param("phoneNumber") String phoneNumber
    );
}
