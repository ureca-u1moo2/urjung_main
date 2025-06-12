package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.user.dto.UserPlanResponseDto;

import java.util.List;

public interface UserPlanService {

    List<UserPlanResponseDto> findAllPlansByUserId(String userId);

}
