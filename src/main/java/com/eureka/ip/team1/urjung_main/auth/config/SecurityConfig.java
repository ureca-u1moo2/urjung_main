package com.eureka.ip.team1.urjung_main.auth.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.eureka.ip.team1.urjung_main.auth.jwt.JwtTokenFilter;
import com.eureka.ip.team1.urjung_main.auth.jwt.TokenProvider;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
	
	private final TokenProvider tokenProvider;
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
    	return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
    		CustomAuthenticationEntryPoint entryPoint
    ) throws Exception {
        return http
        		// form login 관련 disable
        		.httpBasic(httpBasic -> httpBasic.disable())
        		.formLogin(formLogin -> formLogin.disable())
        		.csrf(csrf -> csrf.disable())
        		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        		.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(
                        request -> {
                            // 위 두개의 경로에 대한 요청은 인증/인가 처리를 하지 않겠다.
                            request.requestMatchers(
                                    "/",
                                    "/api/**"  
                                    ).permitAll()
                                    .anyRequest().authenticated();
                        }
                )
                // formLogin 방식에서 허락되지 않는 요청에 대해 자동으로 login.html 페이지로 분기
                // formLogin 을 사용 X -> 예외 발생 -> json 응답 ( login 필요 )
                .exceptionHandling(exceptionHandlingCustomizer 
                		-> exceptionHandlingCustomizer.authenticationEntryPoint(entryPoint))
                // formLogin 방식에서는 Spring Security 가 자동으로 Filter 처리 ( UsernamePasswordAuthenticationFilter )
                // formLogin 을 사용 X -> 위 필터 앞에서 한 번 수행되는 jwt 인증 필터를 적용
                .addFilterBefore(new JwtTokenFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000")); // 허용할 프론트엔드 주소
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}