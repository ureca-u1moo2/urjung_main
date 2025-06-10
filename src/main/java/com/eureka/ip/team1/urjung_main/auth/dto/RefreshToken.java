package com.eureka.ip.team1.urjung_main.auth.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@RedisHash(value = "refresh", timeToLive = 604800) // 7일 (초 단위)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String id; // refreshToken

    private String userId;

//    private String refreshToken; // 실제 토큰 값
}

