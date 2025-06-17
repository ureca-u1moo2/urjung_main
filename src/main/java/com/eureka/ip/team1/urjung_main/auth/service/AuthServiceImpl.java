package com.eureka.ip.team1.urjung_main.auth.service;

import java.time.LocalDate;
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
import com.eureka.ip.team1.urjung_main.common.exception.TokenInvalidException;
import com.eureka.ip.team1.urjung_main.common.exception.UnAuthorizedException;
import com.eureka.ip.team1.urjung_main.membership.entity.Membership;
import com.eureka.ip.team1.urjung_main.membership.repository.MembershipRepository;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.dto.UserResultDto;
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
	
    private final MailService mailService;
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
	        loginResultDto.setToken(tokenDto);
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
	        log.debug("Signup debug: ", e);

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
	        log.debug("Logout debug: ", e);

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
	        authResultDto.setToken(token);

	        log.debug("Reissue successful");

	        return ApiResponse.<AuthResultDto>builder()
	                .result(Result.SUCCESS)
	                .data(authResultDto)
	                .message("Reissue successful")
	                .build();
	    } catch (Exception e) {
	        log.debug("Reissue debug: ", e);

	        return ApiResponse.<AuthResultDto>builder()
	                .result(Result.FAIL)
	                .message("An error occurred during reissue")
	                .build();
	    }
	    
	}

	@Override
	public ApiResponse<UserResultDto> findEmailByNameAndBirth(String name, LocalDate birth) {
		UserResultDto userResultDto = new UserResultDto();
		log.debug("find-id start");
		try {
			String email = userRepository.findByNameAndBirth(name, birth)
					.orElseThrow(() -> new NotFoundException("해당 유저가 없습니다."))
					.getEmail();
			
			UserDto userDto = new UserDto();
			userDto.setEmail(email);
			
			userResultDto.setResult("success");
			userResultDto.setUserDto(userDto);
			
			return ApiResponse.<UserResultDto>builder()
					.result(Result.SUCCESS)
					.data(userResultDto)
					.message("Find email: " + userDto.getEmail())
					.build();
		} catch(Exception e){
			log.debug("Find-email failed: ", e.getMessage());
			
	        return ApiResponse.<UserResultDto>builder()
	                .result(Result.FAIL)
	                .message("Find-email failed: " + e.getMessage())
	                .build();
		}
	}

	@Override
	public ApiResponse<UserResultDto> requestPasswordReset(String email) {
		UserResultDto userResultDto = new UserResultDto();
		log.debug("password-reset start" + email);
		try {
			
			String name = userRepository.findByEmail(email)
					.orElseThrow(() -> new NotFoundException("해당 유저가 없습니다."))
					.getName();
			
			log.debug("name: " + name);
			
			String token = tokenProvider.createPasswordResetToken(email);
			
			// 이메일 전송 서비스 호출
			mailService.sendPasswordResetEmail(email, token);

			userResultDto.setResult("success");
			
			return ApiResponse.<UserResultDto>builder()
					.result(Result.SUCCESS)
					.data(userResultDto)
					.message("Reset Password Success")
					.build();
		}catch(Exception e) {
			log.debug("Reset Password failed: ", e.getMessage());
			
	        return ApiResponse.<UserResultDto>builder()
	                .result(Result.FAIL)
	                .message("Reset Password failed: " + e.getMessage())
	                .build();
		}
	}

	@Override
	public ApiResponse<UserResultDto> resetPassword(String token, String newPassword) {
		UserResultDto userResultDto = new UserResultDto();
		log.debug("Password change start");
		try {
			if(!tokenProvider.validateToken(token)) {
				throw new TokenInvalidException();
			}
			
			String email = tokenProvider.getUsernameFromToken(token);
			
			User user = userRepository.findByEmail(email)
					.orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
			
			log.debug(user.getEmail());
			
			user.changePassword(passwordEncoder.encode(newPassword)); // 비밀번호 변경
			userRepository.save(user);

			UserDto userDto = new UserDto();
			userDto.setName(user.getName());
			userDto.setEmail(user.getEmail());
			userDto.setPassword(user.getPassword());
			
			if(userDto.getPassword().equals(user.getPassword())) {
				userResultDto.setResult("success");
				userResultDto.setUserDto(userDto);
			}
			
			return ApiResponse.<UserResultDto>builder()
					.result(Result.SUCCESS)
					.data(userResultDto)
					.message("Reset Password Success")
					.build();
			
		}catch(Exception e) {
			log.debug("Password change failed: ", e.getMessage());
			
	        return ApiResponse.<UserResultDto>builder()
	                .result(Result.FAIL)
	                .message("Reset Password failed: " + e.getMessage())
	                .build();
		}
	}

}
