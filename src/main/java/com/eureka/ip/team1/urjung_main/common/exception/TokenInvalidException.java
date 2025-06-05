package com.eureka.ip.team1.urjung_main.common.exception;


import static com.eureka.ip.team1.urjung_main.common.Message.TOKEN_INVALID;

// jwt 유효하지 않을 경우 => 401
public class TokenInvalidException extends RuntimeException{
    public TokenInvalidException() {
        super(TOKEN_INVALID);
    }
}
