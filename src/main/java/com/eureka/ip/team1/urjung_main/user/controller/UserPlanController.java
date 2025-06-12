package com.eureka.ip.team1.urjung_main.user.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.user.dto.UserPlanResponseDto;
import com.eureka.ip.team1.urjung_main.user.service.UserPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-plans")
public class UserPlanController {

    private final UserPlanService userPlanService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserPlanResponseDto>>> findAllPlansByUserId(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String userId = userDetails.getUserId();
        List<UserPlanResponseDto> allPlansByUserId = null;

        try {
            allPlansByUserId = userPlanService.findAllPlansByUserId(userId);

            return ResponseEntity.ok(
                    ApiResponse.<List<UserPlanResponseDto>>builder()
                    .result(Result.SUCCESS)
                    .data(allPlansByUserId)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    ApiResponse.<List<UserPlanResponseDto>>builder()
                    .result(Result.FAIL)
                    .message("요금제 데이터를 불러오는 데에 실패하였습니다: " + e.getMessage())
                    .build()
            );
        }
    }

}
