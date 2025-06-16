package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.user.dto.UserDto;

public interface UserService {
	UserDto findById(String userId);
}
