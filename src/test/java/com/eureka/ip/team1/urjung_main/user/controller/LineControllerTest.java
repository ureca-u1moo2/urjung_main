package com.eureka.ip.team1.urjung_main.user.controller;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetails;
import com.eureka.ip.team1.urjung_main.auth.config.SecurityConfig;
import com.eureka.ip.team1.urjung_main.auth.jwt.TokenProvider;
import com.eureka.ip.team1.urjung_main.membership.entity.Membership;
import com.eureka.ip.team1.urjung_main.user.dto.LineDto;
import com.eureka.ip.team1.urjung_main.user.dto.LineSubscriptionDto;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.service.LineSubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LineController.class)
//@Import(SecurityConfig.class)
public class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LineSubscriptionService lineSubscriptionService;

    @MockBean
    private TokenProvider tokenProvider;


    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);

        // 유저 및 멤버십 객체 구성
        Membership membership = new Membership();
        membership.setGiftDiscount(0.1);

        User user = new User();
        user.setUserId("user123");
        user.setName("홍길동");
        user.setEmail("hong@test.com");
        user.setPassword("1234");
        user.setGender("male");
        user.setBirth(LocalDate.of(2000, 1, 1));
        user.setMembership(membership);

        userDetails = new CustomUserDetails(user, List.of());
    }

    @Test
    void subscribeToPlan_shouldReturnSuccess() throws Exception {
        LineSubscriptionDto dto = new LineSubscriptionDto();
        dto.setPlanId("plan123");
        dto.setPhoneNumber("010-0000-0000");

        doNothing().when(lineSubscriptionService).subscribe(any(), any());

        // SecurityContext에 직접 주입
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(post("/api/lines/subscribe")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                        ))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("요금제 가입이 완료되었습니다."));
    }

    @Test
    void cancelLine_shouldReturnSuccess() throws Exception {
        doNothing().when(lineSubscriptionService).cancelLine("user123", "line123");

        mockMvc.perform(delete("/api/lines/line123")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                        ))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("요금제가 정상적으로 해지되었습니다."));
    }

    @Test
    void getDiscountedPrice_shouldReturnSuccess() throws Exception {
        when(lineSubscriptionService.getDiscountedPrice("user123", "plan123")).thenReturn(9000);

        mockMvc.perform(get("/api/lines/discounted-price")
                        .param("planId", "plan123")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(9000));
    }

    // 사용자의 전체 회선 조회 API test
    @Test
    void getAllLinesByUserId_shouldReturnLinesForAuthenticatedUser() throws Exception {
        LineDto dto = LineDto.builder()
                .id("line1")
                .userId("user123")
                .phoneNumber("010-1111-2222")
                .planId("plan001")
                .status("active")
                .startDate(LocalDateTime.now())
                .discountedPrice(25000)
                .build();

        when(lineSubscriptionService.getAllLinesByUserId("user123"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/lines")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].userId").value("user123"));
    }


}
