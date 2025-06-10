package com.eureka.ip.team1.urjung_main.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

}
