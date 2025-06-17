package com.eureka.ip.team1.urjung_main.plan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "plan_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanSummary {

    @Id
    @Column(name = "plan_summary_id", length = 36)
    private String id = UUID.randomUUID().toString();

    @Lob
    private String summaryText;

    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;
}
