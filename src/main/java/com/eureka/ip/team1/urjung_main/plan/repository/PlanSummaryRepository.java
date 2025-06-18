package com.eureka.ip.team1.urjung_main.plan.repository;

import com.eureka.ip.team1.urjung_main.plan.entity.PlanSummary;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanSummaryRepository extends JpaRepository<PlanSummary, String> {
    Optional<PlanSummary> findByPlan(Plan plan);
}
