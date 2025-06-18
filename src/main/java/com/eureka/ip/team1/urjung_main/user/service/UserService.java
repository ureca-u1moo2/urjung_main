package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.dto.UserInfoDto;

public interface UserService {
	UserDto findById(String userId);

	// 마이페이지 내 본인 정보 조회
	UserInfoDto getUserInfoDto(String userName);
}
