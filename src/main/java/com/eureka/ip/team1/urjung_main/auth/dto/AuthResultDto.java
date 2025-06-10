package com.eureka.ip.team1.urjung_main.auth.dto;

import com.eureka.ip.team1.urjung_main.user.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResultDto {
	private String result;
    private String accessToken;
    private Long accessTokenExpiresIn;
    private String refreshToken;
	private UserDto userDto;
}
