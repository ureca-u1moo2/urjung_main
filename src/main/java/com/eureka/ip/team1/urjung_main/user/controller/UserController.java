package com.eureka.ip.team1.urjung_main.user.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.dto.UserInfoDto;
import com.eureka.ip.team1.urjung_main.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 마이페이지 내 본인 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoDto>> getMyInfo
            (@AuthenticationPrincipal CustomUserDetails userDetails)
    {
        UserInfoDto userInfoDto = userService.getUserInfoDto(userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.<UserInfoDto>builder()
                .result(Result.SUCCESS)
                .message("회원 정보 조회에 성공했습니다.")
                .data(userInfoDto)
                .build());
    }
}

