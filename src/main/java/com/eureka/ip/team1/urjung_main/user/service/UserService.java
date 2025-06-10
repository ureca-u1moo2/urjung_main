package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.dto.UserResultDto;

public interface UserService {
	UserResultDto insertUser(UserDto userDto);
}
