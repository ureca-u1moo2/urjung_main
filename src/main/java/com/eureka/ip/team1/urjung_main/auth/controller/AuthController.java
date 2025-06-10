package com.eureka.ip.team1.urjung_main.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.ip.team1.urjung_main.auth.dto.AuthResultDto;
import com.eureka.ip.team1.urjung_main.auth.dto.LoginRequestDto;
import com.eureka.ip.team1.urjung_main.auth.service.AuthService;
import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResultDto>> login(@RequestBody LoginRequestDto loginRequest) {	    
	    log.debug("Login request received: {}", loginRequest);
	    ApiResponse<AuthResultDto> response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
	    log.debug("Response generated: {}", response);
	    HttpStatus status = (response.getResult() == Result.SUCCESS) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
	    return new ResponseEntity<>(response, status);
	}
	
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<AuthResultDto>> signup(@RequestBody UserDto userDto) {
	    log.debug("signup response: " + userDto);
	    ApiResponse<AuthResultDto> response = authService.signup(userDto);

	    HttpStatus status = (response.getResult() == Result.SUCCESS) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
	    return new ResponseEntity<>(response, status);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<AuthResultDto>> logout(@RequestHeader("X-REFRESH-TOKEN") String refreshToken) {
	    ApiResponse<AuthResultDto> response = authService.logout(refreshToken);

	    HttpStatus status = (response.getResult() == Result.SUCCESS) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
	    return new ResponseEntity<>(response, status);
	}

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<AuthResultDto>> refreshAccessToken(@RequestHeader("X-REFRESH-TOKEN") String refreshToken) {
    	ApiResponse<AuthResultDto> response = authService.reissue(refreshToken);

        HttpStatus status = response.getResult() == Result.SUCCESS ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(response, status);
    }
}
