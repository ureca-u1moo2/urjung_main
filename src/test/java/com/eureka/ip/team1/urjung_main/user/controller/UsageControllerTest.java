package com.eureka.ip.team1.urjung_main.user.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.user.dto.UsageRequestDto;
import com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.service.UsageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsageController.class)
class UsageControllerTest {

    @Autowired
    private UsageController usageController;

    @MockitoBean
    private UsageService usageService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        assertNotNull(usageController);
        assertNotNull(usageService);
        assertNotNull(mockMvc);
    }

    @Test
    void getAllUsagesByUserId_성공_Test() throws Exception {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        UsageResponseDto usageResponseDto = UsageResponseDto.builder()
                .planId("plan-123")
                .phoneNumber("010-1234-5678")
                .year(2023)
                .month(10)
                .data(500L)
                .callMinute(100L)
                .message(50L)
                .build();

        when(usageService.getAllUsagesByUserId(any()))
                .thenReturn(List.of(usageResponseDto));
        // When & Then

        mockMvc.perform(get("/api/usages")
                .with(authentication(mockAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].planId").value("plan-123"))
                .andExpect(jsonPath("$.data[0].phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.data[0].year").value(2023))
                .andExpect(jsonPath("$.data[0].month").value(10))
                .andExpect(jsonPath("$.data[0].data").value(500))
                .andExpect(jsonPath("$.data[0].callMinute").value(100))
                .andExpect(jsonPath("$.data[0].message").value(50));
    }

    @Test
    void getAllUsagesByUserId_실패_Test() throws Exception {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        when(usageService.getAllUsagesByUserId(any()))
                .thenThrow(new RuntimeException("DB Error"));

        // When & Then
        mockMvc.perform(get("/api/usages")
                .with(authentication(mockAuth)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("사용량 데이터를 불러오는 데에 실패하였습니다: DB Error"));
    }

    @Test
    void getAllUsagesByUserIdAndMonth_성공_Test() throws Exception {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        UsageResponseDto usageResponseDto = UsageResponseDto.builder()
                .planId("plan-123")
                .phoneNumber("010-1234-5678")
                .year(2023)
                .month(10)
                .data(500L)
                .callMinute(100L)
                .message(50L)
                .build();

        when(usageService.getAllUsagesByUserIdAndMonth(any(UsageRequestDto.class)))
                .thenReturn(List.of(usageResponseDto));

        // When & Then
        mockMvc.perform(get("/api/usages/month")
                .param("year", "2023")
                .param("month", "10")
                .with(authentication(mockAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].planId").value("plan-123"))
                .andExpect(jsonPath("$.data[0].phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.data[0].year").value(2023))
                .andExpect(jsonPath("$.data[0].month").value(10))
                .andExpect(jsonPath("$.data[0].data").value(500))
                .andExpect(jsonPath("$.data[0].callMinute").value(100))
                .andExpect(jsonPath("$.data[0].message").value(50));
    }

    @Test
    void getAllUsagesByUserIdAndMonth_실패_Test() throws Exception {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        when(usageService.getAllUsagesByUserIdAndMonth(any()))
                .thenThrow(new RuntimeException("DB Error"));

        // When & Then
        mockMvc.perform(get("/api/usages/month")
                        .param("year", "2023")
                        .param("month", "10")
                        .with(authentication(mockAuth)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("사용량 데이터를 불러오는 데에 실패하였습니다: DB Error"));
    }

    @Test
    void getCurrentMonthUsagesByUserId_성공_Test() throws Exception{
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        UsageResponseDto usageResponseDto = UsageResponseDto.builder()
                .planId("plan-123")
                .phoneNumber("010-1234-5678")
                .year(2023)
                .month(10)
                .data(500L)
                .callMinute(100L)
                .message(50L)
                .build();

        when(usageService.getCurrentMonthUsagesByUserId(any()))
                .thenReturn(List.of(usageResponseDto));

        // When & Then
        mockMvc.perform(get("/api/usages/current")
                .with(authentication(mockAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].planId").value("plan-123"))
                .andExpect(jsonPath("$.data[0].phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.data[0].year").value(2023))
                .andExpect(jsonPath("$.data[0].month").value(10))
                .andExpect(jsonPath("$.data[0].data").value(500))
                .andExpect(jsonPath("$.data[0].callMinute").value(100))
                .andExpect(jsonPath("$.data[0].message").value(50));
    }

    @Test
    void getCurrentMonthUsagesByUserId_실패_Test() throws Exception {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        when(usageService.getCurrentMonthUsagesByUserId(any()))
                .thenThrow(new RuntimeException("DB Error"));

        // When & Then
        mockMvc.perform(get("/api/usages/current")
                .with(authentication(mockAuth)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("사용량 데이터를 불러오는 데에 실패하였습니다: DB Error"));
    }

    @Test
    void getUsageByLineIdAndMonth_성공_Test() throws Exception {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        UsageResponseDto usageResponseDto = UsageResponseDto.builder()
                .planId("plan-123")
                .phoneNumber("010-1234-5678")
                .year(2023)
                .month(10)
                .data(500L)
                .callMinute(100L)
                .message(50L)
                .build();

        when(usageService.getUsageByLineIdAndMonth(any()))
                .thenReturn(java.util.Optional.of(usageResponseDto));

        // When & Then
        mockMvc.perform(get("/api/usages/lines/{lineId}", "line-123")
                        .with(authentication(mockAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.planId").value("plan-123"))
                .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.data.year").value(2023))
                .andExpect(jsonPath("$.data.month").value(10))
                .andExpect(jsonPath("$.data.data").value(500))
                .andExpect(jsonPath("$.data.callMinute").value(100))
                .andExpect(jsonPath("$.data.message").value(50));
    }

    @Test
    void getUsageByLineIdAndMonth_실패_Test() throws Exception {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        when(usageService.getUsageByLineIdAndMonth(any()))
                .thenThrow(new RuntimeException("DB Error"));

        // When & Then
        mockMvc.perform(get("/api/usages/lines/{lineId}", "line-123")
                .with(authentication(mockAuth)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("사용량 데이터를 불러오는 데에 실패하였습니다: DB Error"));
    }

    @Test
    void getUsageByLineIdAndMonth_존재하지않는라인ID_Test() throws Exception {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        when(usageService.getUsageByLineIdAndMonth(any()))
                .thenReturn(java.util.Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/usages/lines/{lineId}", "non-existing-line-id")
                .with(authentication(mockAuth)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("사용량 데이터를 찾을 수 없습니다."));
    }

    @Test
    void getUsageByUserIdAndPlanIdAndMonth_성공_Test() throws Exception {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        UsageResponseDto usageResponseDto = UsageResponseDto.builder()
                .planId("plan-123")
                .phoneNumber("010-1234-5678")
                .year(2023)
                .month(10)
                .data(500L)
                .callMinute(100L)
                .message(50L)
                .build();

        when(usageService.getUsageByUserIdAndPlanIdAndMonth(any()))
                .thenReturn(java.util.Optional.of(usageResponseDto));

        // When & Then
        mockMvc.perform(get("/api/usages/plans/{planId}", "plan-123")
                        .with(authentication(mockAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.planId").value("plan-123"))
                .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.data.year").value(2023))
                .andExpect(jsonPath("$.data.month").value(10))
                .andExpect(jsonPath("$.data.data").value(500))
                .andExpect(jsonPath("$.data.callMinute").value(100))
                .andExpect(jsonPath("$.data.message").value(50));
    }

    @Test
    void getUsageByUserIdAndPlanIdAndMonth_실패_Test() throws Exception {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        when(usageService.getUsageByUserIdAndPlanIdAndMonth(any()))
                .thenThrow(new RuntimeException("DB Error"));

        // When & Then
        mockMvc.perform(get("/api/usages/plans/{planId}", "plan-123")
                .with(authentication(mockAuth)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("사용량 데이터를 불러오는 데에 실패하였습니다: DB Error"));
    }
}