package com.eureka.ip.team1.urjung_main.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDto {
	private String grantType;
	private String accessToken;
	private Long accessTokenExpiresIn;
	private String refreshToken;
}
