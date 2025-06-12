package com.eureka.ip.team1.urjung_main.auth.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.eureka.ip.team1.urjung_main.auth.config.CustomUserDetailsService;
import com.eureka.ip.team1.urjung_main.auth.dto.TokenDto;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// jwt 생성, 검증 ...
@Component
@RequiredArgsConstructor
@Getter
public class TokenProvider {

	@Value("${myapp.jwt.secret}")
	private String secretKeyStr;
	private SecretKey secretKey;
	
    private final long accessTokenValidDuration = 1000L * 60 * 60; // 1시간
    private final long refreshTokenValidDuration = 1000L * 60 * 60 * 24 * 7; // 7일
	private final CustomUserDetailsService customUserDetailsService;
	
	@PostConstruct
	private void init() {
		System.out.println(secretKeyStr);
		secretKey = new SecretKeySpec(
				secretKeyStr.getBytes(StandardCharsets.UTF_8),
				Jwts.SIG.HS256.key().build().getAlgorithm()
			);
		System.out.println(secretKey);
	}
	
	// jwt 생성
	// username (subject), role
	public String createAccessToken(String username) {
		// 발급일자, 만료일자
		Date now = new Date();
		
		return Jwts.builder()
				.subject(username)
				.issuedAt(now)
				.expiration(new Date(now.getTime() + accessTokenValidDuration))
				.signWith(secretKey, Jwts.SIG.HS256)
				.compact();
	}
	
	public String createRefreshToken(String username) {
		// 발급일자, 만료일자
		Date now = new Date();
		
		return Jwts.builder()
				.subject(username)
				.issuedAt(now)
				.expiration(new Date(now.getTime() + refreshTokenValidDuration))
				.signWith(secretKey, Jwts.SIG.HS256)
				.compact();
	}
	
	public TokenDto generateTokenDto(Authentication authentication) {
		String username = authentication.getName();
		
		String accessToken = createAccessToken(username);
		String refreshToken = createRefreshToken(username);
		
		return TokenDto.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.accessTokenExpiresIn(accessTokenValidDuration)
				.build();
	}
	
	// UserDetailsService 를 통해 사용자 UserDetails 객체를 얻고
	// 이를 통해서 UsernamePasswordAuthenticationToken 객체를 만들어 리턴
	// 유효성 검증을 아래 메소드를 통해서 DB 를 통한 검증을 진행하는 건 token 발급 기간이 길면 발급시점의 UserDetails 와 현재 UserDetails 가 다를 숫 있다는 점 강조
	// 반대로 client 가 접속 할 때마다, DB Access <- 이건 큰 부담
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(this.getUsernameFromToken(token));
		//return new UsernamePasswordAuthenticationToken( userDetails.getUsername(), "", userDetails.getAuthorities());
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}
	
	// jwt 로 부터 username 추출
	public String getUsernameFromToken(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token).getPayload()
				.getSubject();
	}
	
	public String resolveToken(HttpServletRequest request, String headerName) {
		return request.getHeader(headerName);
	}
	
	// jwt 유효성 검증 -> true 이면 유효하다
	// 만료 일자만 검증
	public boolean validateToken(String token) {
		return ! Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token).getPayload()
			.getExpiration().before(new Date());
	}
	
}
