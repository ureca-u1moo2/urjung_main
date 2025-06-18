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

    private final LineSubscriptionService lineSubscriptionService;

    @Override
    public List<UserPlanResponseDto> findAllPlansByUserId(String userId) {
        try {
            List<UserPlanResponseDto> plans = userPlanRepository.findAllPlansByUserId(userId);

            // 실시간 할인 가격으로 덮어쓰기
            plans.forEach(plan -> {
                try {
                    int realTimeDiscountedPrice = lineSubscriptionService.getDiscountedPrice(userId, plan.getPlanId());
                    plan.setDiscountedPrice(realTimeDiscountedPrice);
                } catch (Exception e) {
                    log.warn("실시간 할인 계산 실패 - planId: {}, error: {}", plan.getPlanId(), e.getMessage());
                }
            });

            return plans;
        } catch (Exception e) {
            log.error("요금제 조회 실패: {}", e.getMessage());
            throw new InternalServerErrorException();
        }
    }
//        try {
//            return userPlanRepository.findAllPlansByUserId(userId);
//        } catch (Exception e) {
//            log.info("error : {}", e.getMessage());
//            throw new InternalServerErrorException();
//        }
//    }

}
