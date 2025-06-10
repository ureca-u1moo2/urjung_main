package com.eureka.ip.team1.urjung_main.auth.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    public JwtTokenFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    	
        String accessToken = tokenProvider.resolveToken(request, "X-AUTH-TOKEN");
        String refreshToken = tokenProvider.resolveToken(request, "X-REFRESH-TOKEN");
        
        // Access Token 검증
        if (accessToken != null && tokenProvider.validateToken(accessToken)) {
            Authentication auth = tokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else if (refreshToken != null && tokenProvider.validateToken(refreshToken)) {
            // Refresh Token이 유효한 경우 새로운 Access Token 발급
            String username = tokenProvider.getUsernameFromToken(refreshToken);
            String newAccessToken = tokenProvider.createAccessToken(username);

            // 응답 헤더에 새로운 Access Token 설정
            response.setHeader("X-AUTH-TOKEN", newAccessToken);

            Authentication auth = tokenProvider.getAuthentication(newAccessToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
