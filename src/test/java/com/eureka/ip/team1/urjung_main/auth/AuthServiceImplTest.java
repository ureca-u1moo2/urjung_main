package com.eureka.ip.team1.urjung_main.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eureka.ip.team1.urjung_main.auth.dto.AuthResultDto;
import com.eureka.ip.team1.urjung_main.auth.dto.RefreshToken;
import com.eureka.ip.team1.urjung_main.auth.dto.TokenDto;
import com.eureka.ip.team1.urjung_main.auth.jwt.TokenProvider;
import com.eureka.ip.team1.urjung_main.auth.repository.RefreshTokenRepository;
import com.eureka.ip.team1.urjung_main.auth.service.AuthServiceImpl;
import com.eureka.ip.team1.urjung_main.auth.service.RefreshTokenService;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.membership.entity.Membership;
import com.eureka.ip.team1.urjung_main.membership.repository.MembershipRepository;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
	@InjectMocks
	private AuthServiceImpl authService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private TokenProvider tokenProvider;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MembershipRepository membershipRepository;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private RefreshTokenService refreshTokenService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	void login_Success() {
	    String email = "test@example.com";
	    String password = "password";

	    // Mock authentication
	    Authentication authentication = mock(Authentication.class);
	    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

	    TokenDto tokenDto = TokenDto.builder()
	            .accessToken("jwt-token")
	            .refreshToken("refresh-token")
	            .accessTokenExpiresIn(3600L)
	            .build();

	    when(tokenProvider.generateTokenDto(authentication)).thenReturn(tokenDto);

	    User mockUser = new User();
	    mockUser.setUserId("123");
	    when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

	    ApiResponse<AuthResultDto> response = authService.login(email, password);

	    assertEquals(Result.SUCCESS, response.getResult());
	    assertEquals("jwt-token", response.getData().getAccessToken());
	    assertEquals("refresh-token", response.getData().getRefreshToken());
	    assertEquals(3600L, response.getData().getAccessTokenExpiresIn());
	}


	@Test
	void login_Fail_InvalidCredentials() {
	    String email = "invalid@example.com";
	    String password = "wrongPassword";

	    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
	            .thenThrow(new RuntimeException("Invalid credentials"));

	    ApiResponse<AuthResultDto> response = authService.login(email, password);

	    assertEquals(Result.FAIL, response.getResult());
	    assertEquals("Login failed: Invalid credentials", response.getMessage());
	}

    @Test
    void signup_Success() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        userDto.setName("Test User");
        userDto.setGender("Male");
        userDto.setBirth(LocalDate.now());

        Membership basicMembership = new Membership();
        basicMembership.setMembershipName("basic");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(membershipRepository.findByMembershipName("basic")).thenReturn(Optional.of(basicMembership));
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");

        ApiResponse<AuthResultDto> response = authService.signup(userDto);

        assertEquals(Result.SUCCESS, response.getResult());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signup_Fail_EmailAlreadyExists() {
        UserDto userDto = new UserDto();
        userDto.setEmail("duplicate@example.com");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(new User()));

        ApiResponse<AuthResultDto> response = authService.signup(userDto);

        assertEquals(Result.FAIL, response.getResult());
        assertEquals("Email already exists", response.getMessage());
    }

    @Test
    void signup_Fail_MembershipNotFound() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(membershipRepository.findByMembershipName("basic")).thenReturn(Optional.empty());

        ApiResponse<AuthResultDto> response = authService.signup(userDto);

        assertEquals(Result.FAIL, response.getResult());
        assertEquals("An error occurred during signup", response.getMessage());
    }

    @Test
    void logout_Success() {
        String refreshToken = "valid-refresh-token";
        RefreshToken token = new RefreshToken(refreshToken, "userId");

        when(refreshTokenRepository.findById(refreshToken)).thenReturn(Optional.of(token));

        ApiResponse<AuthResultDto> response = authService.logout(refreshToken);

        assertEquals(Result.SUCCESS, response.getResult());
        verify(refreshTokenRepository, times(1)).delete(token);
    }

    @Test
    void logout_Fail_TokenNotFound() {
        String refreshToken = "invalid-refresh-token";

        when(refreshTokenRepository.findById(refreshToken)).thenReturn(Optional.empty());

        ApiResponse<AuthResultDto> response = authService.logout(refreshToken);

        assertEquals(Result.FAIL, response.getResult());
        assertEquals("An error occurred during logout", response.getMessage());
    }

    @Test
    void reissue_Success() {
        String oldRefreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        String userId = "userId";

        RefreshToken token = new RefreshToken(oldRefreshToken, userId);

        when(tokenProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(refreshTokenRepository.findById(oldRefreshToken)).thenReturn(Optional.of(token));
        when(tokenProvider.createAccessToken(userId)).thenReturn(newAccessToken);
        when(tokenProvider.createRefreshToken(userId)).thenReturn(newRefreshToken);

        ApiResponse<AuthResultDto> response = authService.reissue(oldRefreshToken);

        assertEquals(Result.SUCCESS, response.getResult());
        verify(refreshTokenRepository, times(1)).delete(token);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void reissue_Fail_InvalidToken() {
        String oldRefreshToken = "invalid-refresh-token";

        when(tokenProvider.validateToken(oldRefreshToken)).thenReturn(false);

        ApiResponse<AuthResultDto> response = authService.reissue(oldRefreshToken);

        assertEquals(Result.FAIL, response.getResult());
        assertEquals("An error occurred during reissue", response.getMessage());
    }

}
