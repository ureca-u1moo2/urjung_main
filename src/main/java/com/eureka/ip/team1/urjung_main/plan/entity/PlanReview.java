package com.eureka.ip.team1.urjung_main.plan.entity;

// 요금제 리뷰

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "plan_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanReview {

    @Id
    @Column(name = "review_id", nullable = false, length = 36)
    private String id;

    @Column(name = "plan_id", nullable = false)
    private String planId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "rating")
    private int rating;

    @Column(name = "content")
    private String content;

    @PrePersist
    public void generateId() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
}
