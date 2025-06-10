package com.eureka.ip.team1.urjung_main.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eureka.ip.team1.urjung_main.user.entity.User;

public interface UserRepository extends JpaRepository<User, String>{
	Optional<User> findByEmail(String email);
}
