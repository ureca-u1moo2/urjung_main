package com.eureka.ip.team1.urjung_main.auth.service;

import org.springframework.stereotype.Service;

import com.eureka.ip.team1.urjung_main.auth.dto.RefreshToken;
import com.eureka.ip.team1.urjung_main.auth.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;

	@Override
	public RefreshToken save(String userId, String refreshToken) {

        RefreshToken token = new RefreshToken(refreshToken, userId);
        return refreshTokenRepository.save(token);
	}

}
