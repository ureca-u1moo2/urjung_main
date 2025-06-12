package com.eureka.ip.team1.urjung_main.plan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    @Id
    @Column(name = "tag_id", length = 36)
    private String id;

    @Column(name = "tag_name", nullable = false, unique = true)
    private String tagName;

    @Column(name = "description")
    private String description;

    @PrePersist
    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }
}

