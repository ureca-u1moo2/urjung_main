package com.eureka.ip.team1.urjung_main.membership.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eureka.ip.team1.urjung_main.membership.entity.Membership;

public interface MembershipRepository extends JpaRepository<Membership, String>{
	Optional<Membership> findByMembershipName(String MembershipName);
} 
