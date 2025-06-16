package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.common.exception.ForbiddenException;
import com.eureka.ip.team1.urjung_main.common.exception.InvalidInputException;
import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
import com.eureka.ip.team1.urjung_main.membership.entity.Membership;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import com.eureka.ip.team1.urjung_main.user.dto.LineDto;
import com.eureka.ip.team1.urjung_main.user.dto.LineSubscriptionDto;
import com.eureka.ip.team1.urjung_main.user.entity.Line;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.repository.LineRepository;
import com.eureka.ip.team1.urjung_main.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LineSubscriptionServiceImpl implements LineSubscriptionService {

    private final LineRepository lineRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;


//    // 요금제 가입하기 할인율 적용 전
//    @Override
//    @Transactional
//    public void subscribe(LineSubscriptionDto lineSubscriptionDto, String userId) {
//        String planId = lineSubscriptionDto.getPlanId();
//        String phoneNumber = lineSubscriptionDto.getPhoneNumber();
//        int discountedPrice = lineSubscriptionDto.getDiscountedPrice();
//
//        // 1. 중복 가입 방지
//        boolean alreadyExists = lineRepository.existsByUserIdAndPlanIdAndStatus(userId, planId, Line.LineStatus.active);
//        if (alreadyExists) {
//            throw new IllegalStateException("이미 이 요금제에 가입되어 있습니다.");
//        }
//
//        // 2. 회선 등록
//        Line line = Line.builder()
//                .userId(userId)
//                .planId(planId)
//                .phoneNumber(phoneNumber)
//                .discountedPrice(discountedPrice)
//                .build();
//
//        lineRepository.save(line);
//    }

    // 요금제 가입하기 할인율 적용
    @Override
    @Transactional
    public void subscribe(LineSubscriptionDto lineSubscriptionDto, String userId) {
        String planId = lineSubscriptionDto.getPlanId();
        String phoneNumber = lineSubscriptionDto.getPhoneNumber();

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("요금제를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        Double discountRate = user.getMembership().getGiftDiscount();
        int discountedPrice = calculateDiscountedPrice(plan.getPrice(), discountRate);

        Line line = Line.builder()
                .userId(userId)
                .planId(planId)
                .phoneNumber(phoneNumber)
                .discountedPrice(discountedPrice)
                .status(Line.LineStatus.active)
                .startDate(LocalDateTime.now())
                .build();

        lineRepository.save(line);
    }


    // 요금제 해지하기
    @Transactional
    @Override
    public void cancelLine(String userId, String lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NotFoundException("회선을 찾을 수 없습니다."));

        if (!line.getUserId().equals(userId)) {
            throw new ForbiddenException("본인의 요금제만 해지할 수 있습니다.");
        }

        if (line.getStatus() == Line.LineStatus.canceled) {
            throw new InvalidInputException("이미 해지된 요금제입니다.");
        }

        line.setStatus(Line.LineStatus.canceled);
        line.setEndDate(LocalDateTime.now());
        lineRepository.save(line);
    }

    // 요금제 할인된 가격 조회
    @Override
    public int getDiscountedPrice(String userId, String planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("요금제를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        Double discountRate = user.getMembership().getGiftDiscount();
        return calculateDiscountedPrice(plan.getPrice(), discountRate);
    }

    private int calculateDiscountedPrice(int originalPrice, Double discountRate) {
        if (discountRate == null) discountRate = 0.0;
        return originalPrice - (int)(originalPrice * discountRate);
    }

    //사용자 전체 회선 조회
    @Override
    public List<LineDto> getAllLinesByUserId(String userId) {
        List<Line> lines = lineRepository.findAllByUserId(userId);

        return lines.stream()
                .map(line -> LineDto.builder()
                        .id(line.getId())
                        .userId(line.getUserId())
                        .phoneNumber(line.getPhoneNumber())
                        .planId(line.getPlanId())
                        .status(line.getStatus().name())
                        .startDate(line.getStartDate())
                        .endDate(line.getEndDate())
                        .discountedPrice(line.getDiscountedPrice())
                        .build()
                )
                .collect(Collectors.toList());
    }

}