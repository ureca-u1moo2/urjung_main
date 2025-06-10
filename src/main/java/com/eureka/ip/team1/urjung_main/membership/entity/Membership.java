package com.eureka.ip.team1.urjung_main.membership.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;


@Entity
@Getter
@Setter
public class Membership {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String membershipName;

    @Column(nullable = false)
    private int require;

    @Column(nullable = false)
    private Double giftDiscount;
}
