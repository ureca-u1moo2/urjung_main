package com.eureka.ip.team1.urjung_main.auth.service;

import com.eureka.ip.team1.urjung_main.auth.dto.RefreshToken;

public interface RefreshTokenService {
	RefreshToken save(String userId, String refreshToken);
}
