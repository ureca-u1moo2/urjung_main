package com.eureka.ip.team1.urjung_main.auth.service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eureka.ip.team1.urjung_main.auth.dto.AuthResultDto;
import com.eureka.ip.team1.urjung_main.auth.dto.RefreshToken;
import com.eureka.ip.team1.urjung_main.auth.dto.TokenDto;
import com.eureka.ip.team1.urjung_main.auth.jwt.TokenProvider;
import com.eureka.ip.team1.urjung_main.auth.repository.RefreshTokenRepository;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
import com.eureka.ip.team1.urjung_main.common.exception.UnAuthorizedException;
import com.eureka.ip.team1.urjung_main.membership.entity.Membership;
import com.eureka.ip.team1.urjung_main.membership.repository.MembershipRepository;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
	
	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
	
	@Override
	public ApiResponse<AuthResultDto> login(String email, String password) {
		AuthResultDto loginResultDto = new AuthResultDto();
		
		log.debug("login start");
		
		try {
			Authentication authentication = authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(email, password) );
			
			log.debug("create token");
			
	        User user = userRepository.findByEmail(email)
	                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
	        String userId = user.getUserId();
	        
			TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

	        refreshTokenService.save(userId, tokenDto.getRefreshToken());
	        
			log.debug("created token : " + tokenDto);
			
			log.debug("login end");
			
			UserDto userDto = new UserDto();
			userDto.setUserId(user.getUserId());
			userDto.setName(user.getName());
			userDto.setGender(user.getGender());
			userDto.setBirth(user.getBirth());
			
	        loginResultDto.setResult("success");
	        loginResultDto.setAccessToken(tokenDto.getAccessToken());
	        loginResultDto.setAccessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn());
	        loginResultDto.setRefreshToken(tokenDto.getRefreshToken());
	        loginResultDto.setUserDto(userDto);
	        
	        return ApiResponse.<AuthResultDto>builder()
	                .result(Result.SUCCESS)
	                .data(loginResultDto)
	                .message("Login successful")
	                .build();
		} catch(Exception e) {
			log.debug("login failed: ", e.getMessage());
			
	        return ApiResponse.<AuthResultDto>builder()
	                .result(Result.FAIL)
	                .message("Login failed: " + e.getMessage())
	                .build();
		}
		
	}

	@Override
	public ApiResponse<AuthResultDto> signup(UserDto userDto) {
	    log.debug("Signup start");

	    try {
	        Optional<User> existUser = userRepository.findByEmail(userDto.getEmail());
	        if (existUser.isPresent()) {
	            return ApiResponse.<AuthResultDto>builder()
	                    .result(Result.FAIL)
	                    .message("Email already exists")
	                    .build();
	        }

	        Membership basicMembership = membershipRepository.findByMembershipName("basic")
	        	    .orElseThrow(() -> new RuntimeException("Basic membership not found"));
	        
	        User user = User.builder()
	                .name(userDto.getName())
	                .email(userDto.getEmail())
	                .password(passwordEncoder.encode(userDto.getPassword())) // 비밀번호 암호화
	                .gender(userDto.getGender())
	                .birth(userDto.getBirth())
	                .membership(basicMembership) // Membership 기본값으로 설정예정
	                .build();

	        userRepository.save(user);
	        
	        System.out.println("user :" + user);
	        
	        AuthResultDto authResultDto = new AuthResultDto();
	        authResultDto.setResult("success");

	        log.debug("Signup successful");

	        return ApiResponse.<AuthResultDto>builder()
	                .result(Result.SUCCESS)
	                .data(authResultDto)
	                .message("Signup successful")
	                .build();
	    } catch (Exception e) {
	        log.error("Signup error: ", e);

	        return ApiResponse.<AuthResultDto>builder()
	                .result(Result.FAIL)
	                .message("An error occurred during signup")
	                .build();
	    }
	}

	@Override
	public ApiResponse<AuthResultDto> logout(String RefreshToken) {
	    try {
			RefreshToken saved = refreshTokenRepository.findById(RefreshToken)
					.orElseThrow(() -> new NotFoundException("불일치" + RefreshToken));
			
			refreshTokenRepository.delete(saved);
	        
	        AuthResultDto authResultDto = new AuthResultDto();
	        authResultDto.setResult("success");

	        log.debug("Logout successful");

	        return ApiResponse.<AuthResultDto>builder()
	                .result(Result.SUCCESS)
	                .data(authResultDto)
	                .message("Logout successful")
	                .build();
	    } catch (Exception e) {
	        log.error("Logout error: ", e);

	        return ApiResponse.<AuthResultDto>builder()
	                .result(Result.FAIL)
	                .message("An error occurred during logout")
	                .build();
	    }
	}
	
	@Override
	public ApiResponse<AuthResultDto> reissue(String oldRefreshToken) {
	    log.debug("Reissue start");

	    try {
			if (!tokenProvider.validateToken(oldRefreshToken)) {
				throw new UnAuthorizedException();
			}
			System.out.println("RefreshToken : " + oldRefreshToken);
			RefreshToken saved = refreshTokenRepository.findById(oldRefreshToken)
					.orElseThrow(() -> new NotFoundException("없는 토큰" + oldRefreshToken));
			
			String username = saved.getUserId();
			
			String newAccessToken = tokenProvider.createAccessToken(username);
			String newRefreshToken = tokenProvider.createRefreshToken(username);
			
			refreshTokenRepository.delete(saved);
			refreshTokenRepository.save(new RefreshToken(newRefreshToken, username));
			
	        TokenDto token = TokenDto.builder()
					.accessToken(newAccessToken)
					.refreshToken(newRefreshToken)
					.accessTokenExpiresIn(tokenProvider.getAccessTokenValidDuration())
					.build();
	        
	        System.out.println("token :" + token);
	        
	        AuthResultDto authResultDto = new AuthResultDto();
	        authResultDto.setResult("success");

	        log.debug("Reissue successful");

	        return ApiResponse.<AuthResultDto>builder()
	                .result(Result.SUCCESS)
	                .data(authResultDto)
	                .message("Reissue successful")
	                .build();
	    } catch (Exception e) {
	        log.error("Reissue error: ", e);

	        return ApiResponse.<AuthResultDto>builder()
	                .result(Result.FAIL)
	                .message("An error occurred during reissue")
	                .build();
	    }
	    
	}

}
