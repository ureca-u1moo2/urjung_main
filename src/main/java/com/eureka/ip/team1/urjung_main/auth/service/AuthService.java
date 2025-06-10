package com.eureka.ip.team1.urjung_main.auth.service;

import com.eureka.ip.team1.urjung_main.auth.dto.AuthResultDto;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;

public interface AuthService {
	ApiResponse<AuthResultDto> login(String email, String password);
	ApiResponse<AuthResultDto> signup(UserDto userDto);
	ApiResponse<AuthResultDto> logout(String RefreshToken);
	
	
	ApiResponse<AuthResultDto> reissue(String oldRefreshToken);
}
