package com.eureka.ip.team1.urjung_main.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Setter
@Getter
public class MonthlyUsage {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID monthlyUsageId;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Column(nullable = false)
    private LocalDate month;

    @Column(nullable = false)
    private Long data;

    @Column(nullable = false)
    private Long callMinute;

    @Column(nullable = false)
    private Long message;
}
