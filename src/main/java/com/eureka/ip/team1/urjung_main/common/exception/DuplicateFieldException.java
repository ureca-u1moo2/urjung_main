package com.eureka.ip.team1.urjung_main.common.exception;

// 중복된 값 => 409
public class DuplicateFieldException extends RuntimeException {
    public DuplicateFieldException(String message) {
        super(message);
    }

}
