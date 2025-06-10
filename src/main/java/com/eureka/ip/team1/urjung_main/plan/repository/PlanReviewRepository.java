package com.eureka.ip.team1.urjung_main.plan.repository;

import com.eureka.ip.team1.urjung_main.plan.entity.PlanReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanReviewRepository extends JpaRepository<PlanReview, String> {

    // 요금제 리뷰 목록 조회
    List<PlanReview> findByPlanId(String planId);
}
