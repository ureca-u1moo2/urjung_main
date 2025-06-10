package com.eureka.ip.team1.urjung_main.auth.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.eureka.ip.team1.urjung_main.auth.dto.RefreshToken;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String>{
	Optional<RefreshToken> findById(String token);
//	Optional<RefreshToken> findUuidByRefreshToken(String token);
}
