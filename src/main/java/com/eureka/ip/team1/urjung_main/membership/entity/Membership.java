package com.eureka.ip.team1.urjung_main.membership.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="membership")
@Getter
@Setter
public class Membership {
	@Id
	@Column(updatable = false, nullable = false, length = 36)
	private String membershipId;
	
	@Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'basic'")
	private String membershipName;

	@Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'Default'")
	private String requirement;
	
    @PrePersist
    public void setDefaultValues() {
        this.membershipId = UUID.randomUUID().toString();
    }
}
