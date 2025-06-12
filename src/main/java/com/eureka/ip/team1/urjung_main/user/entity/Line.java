package com.eureka.ip.team1.urjung_main.user.entity;

import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "line")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Line {

    @Id
    @Column(name = "line_id")
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "plan_id", nullable = false, updatable = false, length = 36)
    private String planId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('active', 'canceled') DEFAULT 'active'")
//    @Column(name = "status", nullable = false)
    private LineStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    // 요금제 할인 후 실제 가입 가격
    @Column(name = "discounted_price", nullable = false)
    private int discountedPrice;

    // 요금제 해지 일
//    @Column(name = "terminated_at")
//    private LocalDateTime terminatedAt;

    // Plan 연관 객체 (조회용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", insertable = false, updatable = false)
    private Plan plan;

    @PrePersist
    public void onCreate() {
        this.id = UUID.randomUUID().toString();
        this.startDate = LocalDateTime.now();
        this.status = LineStatus.active;
    }

    public enum LineStatus {
        active, canceled
    }
}


//package com.eureka.ip.team1.urjung_main.plan.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "line")
//@Getter
//@Setter
//@NoArgsConstructor
//public class Line {
//
//    @Id
//    @Column(name = "line_id")
//    private String id;
//
//    @Column(name = "user_id", nullable = false)
//    private String userId;
//
//    @Column(name = "plan_id", nullable = false)
//    private String planId;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", columnDefinition = "ENUM('active', 'canceled') DEFAULT 'active'")
//    private LineStatus status;
//
//    @Column(name = "start_date")
//    private LocalDateTime startDate;
//
//    @Column(name = "end_date")
//    private LocalDateTime endDate;
//
//    @Column(name = "phone_number", unique = true)
//    private String phoneNumber;
//
//    public enum LineStatus {
//        active, canceled
//    }
//}

