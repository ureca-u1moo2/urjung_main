package com.eureka.ip.team1.urjung_main.user.repository;

import com.eureka.ip.team1.urjung_main.user.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, String> {

    // 사용자 전체 회선 조회
    List<Line> findAllByUserId(String userId);

    boolean existsByUserIdAndPlanIdAndStatus(String userId, String planId, Line.LineStatus status);

    // 기존 취소 했던 회선 다시 재활성
    Optional<Line> findByPhoneNumber(String phoneNumber);

}
