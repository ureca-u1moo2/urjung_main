package com.eureka.ip.team1.urjung_main.auth.service;

import java.time.LocalDate;

import com.eureka.ip.team1.urjung_main.auth.dto.AuthResultDto;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.dto.UserResultDto;

public interface AuthService {
	ApiResponse<AuthResultDto> login(String email, String password);
	ApiResponse<AuthResultDto> signup(UserDto userDto);
	ApiResponse<AuthResultDto> logout(String RefreshToken);
	
	
	ApiResponse<AuthResultDto> reissue(String oldRefreshToken);

	ApiResponse<UserResultDto> findEmailByNameAndBirth(String name, LocalDate birth);
	ApiResponse<UserResultDto> requestPasswordReset(String email);
	ApiResponse<UserResultDto> resetPassword(String token, String newPassword);
}
