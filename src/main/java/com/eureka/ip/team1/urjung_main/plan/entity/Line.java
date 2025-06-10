package com.eureka.ip.team1.urjung_main.plan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "line")
@Getter
@Setter
@NoArgsConstructor
public class Line {

    @Id
    @Column(name = "line_id")
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "plan_id", nullable = false)
    private String planId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('active', 'canceled') DEFAULT 'active'")
    private LineStatus status;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    public enum LineStatus {
        active, canceled
    }
}

