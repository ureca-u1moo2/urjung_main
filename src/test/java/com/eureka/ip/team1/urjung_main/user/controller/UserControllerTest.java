package com.eureka.ip.team1.urjung_main.user.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.user.controller.UserController;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.dto.UserInfoDto;
import com.eureka.ip.team1.urjung_main.user.service.UserService;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .userId("user-100")
                .email("test@example.com")
                .password("pw1234")
                .build();

        userDetails = CustomUserDetails.builder()
                .user(user)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        // Spring Security Context에 직접 등록
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
    }

    @Test
    @DisplayName("회원 정보 조회 성공 - SecurityContext 기반")
    void testGetMyInfoSuccess() throws Exception {
        // given
        UserInfoDto dto = UserInfoDto.builder()
                .name("홍길동")
                .email("test@example.com")
                .gender("M")
                .birth(LocalDate.of(1995, 5, 5))
                .membershipName("프리미엄")
                .build();

        when(userService.getUserInfoDto("user-100")).thenReturn(dto);

        // when & then
        mockMvc.perform(get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andExpect(jsonPath("$.message").value("회원 정보 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.gender").value("M"))
                .andExpect(jsonPath("$.data.birth").value("1995-05-05"))
                .andExpect(jsonPath("$.data.membershipName").value("프리미엄"));

        verify(userService).getUserInfoDto("user-100");
    }
}
