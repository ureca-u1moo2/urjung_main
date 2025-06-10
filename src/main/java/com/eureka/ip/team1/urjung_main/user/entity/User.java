package com.eureka.ip.team1.urjung_main.user.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.eureka.ip.team1.urjung_main.membership.entity.Membership;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@Column(updatable = false, nullable = false, length = 36)
	private String userId;
	
	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String gender;

	@Column(nullable = false)
	private LocalDate birth;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="membership_id", nullable=false)
	private Membership membership;
	
    @PrePersist
    public void generateId() {
        this.userId = UUID.randomUUID().toString();
    }
}
