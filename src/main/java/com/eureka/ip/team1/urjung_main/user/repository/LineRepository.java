package com.eureka.ip.team1.urjung_main.user.repository;

import com.eureka.ip.team1.urjung_main.user.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, String> {

    // 사용자 전체 회선 조회
    List<Line> findAllByUserId(String userId);

    boolean existsByUserIdAndPlanIdAndStatus(String userId, String planId, Line.LineStatus status);
}
