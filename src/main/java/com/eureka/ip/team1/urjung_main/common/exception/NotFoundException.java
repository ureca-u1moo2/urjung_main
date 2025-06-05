package com.eureka.ip.team1.urjung_main.common.exception;

// 데이터 존재 x (ex : 해당 아이디의 유저가 없을 때) => 404
public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
