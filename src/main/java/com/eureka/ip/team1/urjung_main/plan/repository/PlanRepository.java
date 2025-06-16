package com.eureka.ip.team1.urjung_main.plan.repository;

import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// 요금제 목록 및 상세 비교 Repository
public interface PlanRepository extends JpaRepository<Plan, String> {

    // 인기순(가입자 수 count)
    @Query("SELECT p.id FROM Plan p LEFT JOIN Line l ON p.id = l.planId AND l.status = 'active' " +
            "GROUP BY p.id ORDER BY COUNT(l.id) DESC")
    List<String> findPopularPlans();

    @Query("SELECT DISTINCT p FROM Plan p " +
            "LEFT JOIN FETCH p.tags " +
            "WHERE p.id IN (:ids) " +
            "ORDER BY (SELECT COUNT(l.id) FROM Line l WHERE l.planId = p.id AND l.status = 'active') DESC")
    List<Plan> findByIdsWithTags(@Param("ids") List<String> ids);

    // 가격 낮은 순
    @Query("SELECT DISTINCT p FROM Plan p JOIN FETCH p.tags t ORDER BY p.price ASC")
    List<Plan> findAllByOrderByPriceAsc();

    // 가격 높은 순
    @Query("SELECT DISTINCT p FROM Plan p JOIN FETCH p.tags t ORDER BY p.price DESC")
    List<Plan> findAllByOrderByPriceDesc();

    // 데이터 적은 순
    @Query("""
            SELECT DISTINCT p FROM Plan p JOIN FETCH p.tags t ORDER BY
            CASE WHEN p.dataAmount = -1
                        THEN 999999999999
                        ELSE p.dataAmount
            END ASC
            """)
    List<Plan> findAllByOrderByDataAmountAsc();

    // 데이터 많은 순
    @Query("""
            SELECT DISTINCT p FROM Plan p JOIN FETCH p.tags t ORDER BY
            CASE WHEN p.dataAmount = -1
                        THEN 999999999999
                        ELSE p.dataAmount
            END DESC
            """)
    List<Plan> findAllByOrderByDataAmountDesc();


}
