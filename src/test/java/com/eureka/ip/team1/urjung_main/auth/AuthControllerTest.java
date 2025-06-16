package com.eureka.ip.team1.urjung_main.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eureka.ip.team1.urjung_main.auth.config.CustomAuthenticationEntryPoint;
import com.eureka.ip.team1.urjung_main.auth.config.SecurityConfig;
import com.eureka.ip.team1.urjung_main.auth.controller.AuthController;
import com.eureka.ip.team1.urjung_main.auth.dto.AuthResultDto;
import com.eureka.ip.team1.urjung_main.auth.jwt.TokenProvider;
import com.eureka.ip.team1.urjung_main.auth.service.AuthService;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.dto.UserResultDto;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private TokenProvider jwtUtil;
    
    @MockitoBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Success() throws Exception {
        AuthResultDto authResultDto = new AuthResultDto();
        authResultDto.setResult("success");
        authResultDto.setAccessToken("jwt-token");

        ApiResponse<AuthResultDto> apiResponse = ApiResponse.<AuthResultDto>builder()
                .result(Result.SUCCESS)
                .data(authResultDto)
                .message("Login successful")
                .build();

        when(authService.login(anyString(), anyString())).thenReturn(apiResponse);

        this.mockMvc.perform(post("/api/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"test@example.com\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token"));
    }
    
    @Test
    void login_UserNotFound() throws Exception {
        ApiResponse<AuthResultDto> apiResponse = ApiResponse.<AuthResultDto>builder()
                .result(Result.FAIL)
                .message("User not found")
                .build();

        when(authService.login(anyString(), anyString())).thenReturn(apiResponse);

        this.mockMvc.perform(post("/api/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"notfound@example.com\", \"password\": \"password\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }                
       
    @Test
    void signup_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        userDto.setName("Test User");

        ApiResponse<AuthResultDto> apiResponse = ApiResponse.<AuthResultDto>builder()
                .result(Result.SUCCESS)
                .message("Signup successful")
                .build();

        when(authService.signup(any(UserDto.class))).thenReturn(apiResponse);

        mockMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"test@example.com\", \"password\": \"password\", \"name\": \"Test User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Signup successful"));
    }
    
    @Test
    void signup_EmailAlreadyExists() throws Exception {
        ApiResponse<AuthResultDto> apiResponse = ApiResponse.<AuthResultDto>builder()
                .result(Result.FAIL)
                .message("Email already exists")
                .build();

        when(authService.signup(any(UserDto.class))).thenReturn(apiResponse);

        mockMvc.perform(post("/api/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"existing@example.com\", \"password\": \"password\", \"name\": \"Test User\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void signup_MissingRequiredFields() throws Exception {
        ApiResponse<AuthResultDto> apiResponse = ApiResponse.<AuthResultDto>builder()
                .result(Result.FAIL)
                .message("Required fields are missing")
                .build();

        when(authService.signup(any(UserDto.class))).thenReturn(apiResponse);

        mockMvc.perform(post("/api/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"\", \"password\": \"\", \"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("Required fields are missing"));
    }
    
    @Test
    void logout_Success() throws Exception {
        ApiResponse<AuthResultDto> apiResponse = ApiResponse.<AuthResultDto>builder()
                .result(Result.SUCCESS)
                .message("Logout successful")
                .build();

        when(authService.logout(anyString())).thenReturn(apiResponse);

        mockMvc.perform(post("/api/logout")
                .header("X-REFRESH-TOKEN", "valid-refresh-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    void logout_InvalidToken() throws Exception {
        ApiResponse<AuthResultDto> apiResponse = ApiResponse.<AuthResultDto>builder()
                .result(Result.FAIL)
                .message("Invalid refresh token")
                .build();

        when(authService.logout(anyString())).thenReturn(apiResponse);

        mockMvc.perform(post("/api/logout")
                .header("X-REFRESH-TOKEN", "invalid-refresh-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
    }

    @Test
    void refreshAccessToken_Success() throws Exception {
        AuthResultDto authResultDto = new AuthResultDto();
        authResultDto.setResult("success");
        authResultDto.setAccessToken("new-jwt-token");

        ApiResponse<AuthResultDto> apiResponse = ApiResponse.<AuthResultDto>builder()
                .result(Result.SUCCESS)
                .data(authResultDto)
                .message("Token reissued successfully")
                .build();

        when(authService.reissue(anyString())).thenReturn(apiResponse);

        mockMvc.perform(post("/api/auth/refresh")
                .header("X-REFRESH-TOKEN", "valid-refresh-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").value("new-jwt-token"));
    }

    @Test
    void refreshAccessToken_InvalidToken() throws Exception {
        ApiResponse<AuthResultDto> apiResponse = ApiResponse.<AuthResultDto>builder()
                .result(Result.FAIL)
                .message("Invalid refresh token")
                .build();

        when(authService.reissue(anyString())).thenReturn(apiResponse);

        mockMvc.perform(post("/api/auth/refresh")
                .header("X-REFRESH-TOKEN", "invalid-refresh-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
    }

    @Test
    void findId_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("홍길동");
        userDto.setBirth(LocalDate.of(1990, 1, 1));

        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("success");
        UserDto foundUserDto = new UserDto();
        foundUserDto.setEmail("hong@example.com");
        resultDto.setUserDto(foundUserDto);

        ApiResponse<UserResultDto> response = ApiResponse.<UserResultDto>builder()
                .result(Result.SUCCESS)
                .data(resultDto)
                .message("Find email: hong@example.com")
                .build();

        when(authService.findEmailByNameAndBirth(eq("홍길동"), eq(LocalDate.of(1990, 1, 1))))
                .thenReturn(response);

        mockMvc.perform(post("/api/users/find-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"홍길동\",\"birth\":\"1990-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.userDto.email").value("hong@example.com"));
    }

    @Test
    void findId_Fail() throws Exception {
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("fail");

        ApiResponse<UserResultDto> response = ApiResponse.<UserResultDto>builder()
                .result(Result.FAIL)
                .message("Find-email failed: 해당 유저가 없습니다.")
                .build();

        when(authService.findEmailByNameAndBirth(eq("없는사람"), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/users/find-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"없는사람\",\"birth\":\"2000-01-01\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("Find-email failed: 해당 유저가 없습니다."));
    }
    
    @Test
    void requestPasswordReset_Success() throws Exception {
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("success");

        ApiResponse<UserResultDto> response = ApiResponse.<UserResultDto>builder()
                .result(Result.SUCCESS)
                .data(resultDto)
                .message("Reset Password Success")
                .build();

        when(authService.requestPasswordReset(eq("hong@example.com"))).thenReturn(response);

        mockMvc.perform(post("/api/users/find-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"hong@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"));
    }

    @Test
    void requestPasswordReset_Fail() throws Exception {
        ApiResponse<UserResultDto> response = ApiResponse.<UserResultDto>builder()
                .result(Result.FAIL)
                .message("Reset Password failed: 해당 유저가 없습니다.")
                .build();

        when(authService.requestPasswordReset(eq("notfound@example.com"))).thenReturn(response);

        mockMvc.perform(post("/api/users/find-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"notfound@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("Reset Password failed: 해당 유저가 없습니다."));
    }

    @Test
    void resetPassword_Success() throws Exception {
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("success");

        ApiResponse<UserResultDto> response = ApiResponse.<UserResultDto>builder()
                .result(Result.SUCCESS)
                .data(resultDto)
                .message("Reset Password Success")
                .build();

        when(authService.resetPassword(eq("valid-token"), eq("newPassword"))).thenReturn(response);

        mockMvc.perform(post("/api/users/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"valid-token\",\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"));
    }

    @Test
    void resetPassword_Fail() throws Exception {
        ApiResponse<UserResultDto> response = ApiResponse.<UserResultDto>builder()
                .result(Result.FAIL)
                .message("Reset Password failed: 유효하지 않은 토큰")
                .build();

        when(authService.resetPassword(eq("invalid-token"), eq("newPassword"))).thenReturn(response);

        mockMvc.perform(post("/api/users/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"invalid-token\",\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("Reset Password failed: 유효하지 않은 토큰"));
    }

}
