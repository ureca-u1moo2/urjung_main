package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.common.exception.InternalServerErrorException;
import com.eureka.ip.team1.urjung_main.user.dto.UserPlanResponseDto;
import com.eureka.ip.team1.urjung_main.user.repository.UserPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPlanServiceImpl implements UserPlanService {

    private final UserPlanRepository userPlanRepository;

    @Override
    public List<UserPlanResponseDto> findAllPlansByUserId(String userId) {
        try {
            return userPlanRepository.findAllPlansByUserId(userId);
        } catch (Exception e) {
            log.info("error : {}", e.getMessage());
            throw new InternalServerErrorException();
        }
    }

}
