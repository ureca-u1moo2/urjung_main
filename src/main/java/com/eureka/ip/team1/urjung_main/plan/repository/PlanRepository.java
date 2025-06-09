package com.eureka.ip.team1.urjung_main.plan.repository;

import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// 요금제 목록 및 상세 비교 Repository
public interface PlanRepository extends JpaRepository<Plan, String> {

    // 인기순(가입자 수 count)
    @Query("SELECT p FROM Plan p LEFT JOIN Line l ON p.id = l.planId AND l.status = 'active' " +
            "GROUP BY p.id ORDER BY COUNT(l.id) DESC")
    List<Plan> findPopularPlans();

    // 가격 낮은 순
    List<Plan> findAllByOrderByPriceAsc();

    // 가격 높은 순
    List<Plan> findAllByOrderByPriceDesc();

    // 데이터 적은 순
    List<Plan> findAllByOrderByDataAmountAsc();

    // 데이터 많은 순
    List<Plan> findAllByOrderByDataAmountDesc();


}
