package com.eureka.ip.team1.urjung_main.common.exception;

import static com.eureka.ip.team1.urjung_main.common.Message.FORBIDDEN;

// 권한이 없는 사용자 접근 => 403
public class ForbiddenException extends RuntimeException{
    public ForbiddenException() {
        super(FORBIDDEN);
    }
    public ForbiddenException(String message) { super(message);} // 리뷰 본인 작성글만 수정 가능
}
