package com.eureka.ip.team1.urjung_main.user.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import com.eureka.ip.team1.urjung_main.user.dto.LineSubscriptionDto;
import com.eureka.ip.team1.urjung_main.user.repository.UserRepository;
import com.eureka.ip.team1.urjung_main.user.service.LineSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lines")
@RequiredArgsConstructor
public class LineController {

    private final LineSubscriptionService lineSubscriptionService;


    // 요금제 가입 하기
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<Void>> subscribeToPlan(
            @RequestBody LineSubscriptionDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        lineSubscriptionService.subscribe(requestDto, userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .result(Result.SUCCESS)
                .data(null)
                .message("요금제 가입이 완료되었습니다.")
                .build());
    }

    // 요금제 해지 하기
    @DeleteMapping("/{lineId}")
    public ResponseEntity<ApiResponse<Void>> cancelLine(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String lineId) {

        lineSubscriptionService.cancelLine(userDetails.getUserId(), lineId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .result(Result.SUCCESS)
                .message("요금제가 정상적으로 해지되었습니다.")
                .build());
    }

    // 요금제 할인 적용된 가격 확인
    @GetMapping("/discounted-price")
    public ResponseEntity<ApiResponse<Integer>> getDiscountedPrice(
            @RequestParam String planId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        int discountedPrice = lineSubscriptionService.getDiscountedPrice(userDetails.getUserId(), planId);

        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .result(Result.SUCCESS)
                .message("SUCCESS")
                .data(discountedPrice)
                .build());
    }
}
