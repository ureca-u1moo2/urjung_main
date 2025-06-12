package com.eureka.ip.team1.urjung_main.plan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "plan_tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanTag {
    @Id
    @Column(name = "plan_tag_id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @PrePersist
    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }
}
