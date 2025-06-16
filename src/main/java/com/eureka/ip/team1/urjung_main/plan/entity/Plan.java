package com.eureka.ip.team1.urjung_main.plan.entity;

// 요금제 전체 목록, 상세 조회
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @Column(name = "plan_id", nullable = false, length = 36)
    private String id;

    @Column(name = "plan_name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "description")
    private String description;

    @Column(name = "data_amount")
    private Long dataAmount;

    @Column(name = "call_amount")
    private Long callAmount;

    @Column(name = "sms_amount")
    private Long smsAmount;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable(name = "plan_tag",
            joinColumns = @JoinColumn(name = "plan_id", referencedColumnName = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id")
    )
    private List<Tag> tags;

    // 생성 시 UUID 자동 세팅
    @PrePersist
    public void generateId() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = java.time.LocalDateTime.now();
    }
}

