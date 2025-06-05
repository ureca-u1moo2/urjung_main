package com.eureka.ip.team1.urjung_main.common.exception;

// 데이터베이스 오류 => 500
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
}
