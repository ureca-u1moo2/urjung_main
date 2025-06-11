package com.eureka.ip.team1.urjung_main.user.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.user.dto.UsageRequestDto;
import com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto;
import com.eureka.ip.team1.urjung_main.user.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usages")
public class UsageController {

    private final UsageService usageService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsageResponseDto>>> getAllUsagesByUserId(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String userId = userDetails.getUserId();

        UsageRequestDto usageRequestDto = new UsageRequestDto();
        usageRequestDto.setUserId(userId);

        List<UsageResponseDto> allUsagesByUserId = null;

        try{
            allUsagesByUserId = usageService.getAllUsagesByUserId(usageRequestDto);

            return buildSuccessResponse(allUsagesByUserId);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @GetMapping("/month")
    public ResponseEntity<ApiResponse<List<UsageResponseDto>>> getAllUsageByUserIdAndMonth(
            UsageRequestDto usageRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String userId = userDetails.getUserId();

        usageRequestDto.setUserId(userId);

        List<UsageResponseDto> allUsagesByUserIdAndMonth = null;

        try{
            allUsagesByUserIdAndMonth = usageService.getAllUsagesByUserIdAndMonth(usageRequestDto);

            return buildSuccessResponse(allUsagesByUserIdAndMonth);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<List<UsageResponseDto>>> getCurrentMonthUsagesByUserId(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String userId = userDetails.getUserId();

        UsageRequestDto usageRequestDto = new UsageRequestDto();
        usageRequestDto.setUserId(userId);

        List<UsageResponseDto> currentMonthUsagesByUserId = null;

        try{
            currentMonthUsagesByUserId = usageService.getCurrentMonthUsagesByUserId(usageRequestDto);

            return buildSuccessResponse(currentMonthUsagesByUserId);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<ApiResponse<UsageResponseDto>> getUsageByLineIdAndMonth(
            UsageRequestDto usageRequestDto,
            @PathVariable("lineId") String lineId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String userId = userDetails.getUserId();

        usageRequestDto.setUserId(userId);
        usageRequestDto.setLineId(lineId);

        Optional<UsageResponseDto> usageResponseDto = Optional.empty();

        try {
            usageResponseDto = usageService.getUsageByLineIdAndMonth(usageRequestDto);
            return usageResponseDto
                    .map(this::buildSuccessResponse)
                    .orElseGet(this::buildNotFoundResponse);

        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @GetMapping("/plans/{planId}")
    public ResponseEntity<ApiResponse<UsageResponseDto>> getUsageByUserIdAndPlanIdAndMonth(
            UsageRequestDto usageRequestDto,
            @PathVariable("planId") String planId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        String userId = userDetails.getUserId();
        String userId = "4fcdaa60-7f04-4850-86f7-a391f9077fce";

        usageRequestDto.setUserId(userId);
        usageRequestDto.setPlanId(planId);

        Optional<UsageResponseDto> usageResponseDto = Optional.empty();

        try {
           usageResponseDto = usageService.getUsageByUserIdAndPlanIdAndMonth(usageRequestDto);

            return usageResponseDto
                    .map(this::buildSuccessResponse)
                    .orElseGet(this::buildNotFoundResponse);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> buildErrorResponse(String message) {
        return ResponseEntity.status(500).body(
                ApiResponse.<T>builder()
                        .result(Result.FAIL)
                        .message("사용량 데이터를 불러오는 데에 실패하였습니다: " + message)
                        .build()
        );
    }

    private <T> ResponseEntity<ApiResponse<T>> buildNotFoundResponse() {
        return ResponseEntity.status(404).body(
                ApiResponse.<T>builder()
                        .result(Result.NOT_FOUND)
                        .message("사용량 데이터를 찾을 수 없습니다.")
                        .build()
        );
    }

    private <T> ResponseEntity<ApiResponse<T>> buildSuccessResponse(T data) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .result(Result.SUCCESS)
                        .data(data)
                        .build()
        );
    }

}
