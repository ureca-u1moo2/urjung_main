package com.eureka.ip.team1.urjung_main.user.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.user.dto.UserPlanResponseDto;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.service.UserPlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserPlanController.class)
class UserPlanControllerTest {

    @Autowired
    private UserPlanController userPlanController;

    @MockitoBean
    private UserPlanService userPlanService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        assertNotNull(userPlanController);
        assertNotNull(userPlanService);
        assertNotNull(mockMvc);
    }

    @Test
    void getAllPlansByUserId_성공_Test() throws Exception {
        //Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        UserPlanResponseDto userPlanResponseDto = UserPlanResponseDto.builder()
                .planId("plan-001")
                .planName("기본 요금제")
                .phoneNumber("123-1234-1234")
                .description("기본 요금제 설명")
                .startDate(LocalDateTime.of(2023, 10, 1, 0, 0))
                .discountedPrice(10000)
                .build();

        when(userPlanService.findAllPlansByUserId("test-user-id"))
                .thenReturn(List.of(userPlanResponseDto));

        mockMvc.perform(get("/api/user-plans")
                .with(authentication(mockAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].planId").value("plan-001"))
                .andExpect(jsonPath("$.data[0].planName").value("기본 요금제"))
                .andExpect(jsonPath("$.data[0].phoneNumber").value("123-1234-1234"))
                .andExpect(jsonPath("$.data[0].description").value("기본 요금제 설명"))
                .andExpect(jsonPath("$.data[0].startDate").value("2023-10-01T00:00:00"))
                .andExpect(jsonPath("$.data[0].discountedPrice").value(10000));
    }

    @Test
    void getAllPlansByUserId_실패_Test() throws Exception {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("test-user-id");

        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser, List.of());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        when(userPlanService.findAllPlansByUserId("test-user-id"))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/user-plans")
                .with(authentication(mockAuth)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("요금제 데이터를 불러오는 데에 실패하였습니다: Database error"));
    }
}