package com.eureka.ip.team1.urjung_main.plan.repository;

import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

// 요금제 목록 및 상세 비교 Repository
public interface PlanRepository extends JpaRepository<Plan, String> {

}
