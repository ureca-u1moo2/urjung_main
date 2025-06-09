package com.eureka.ip.team1.urjung_main.common.exception;

import static com.eureka.ip.team1.urjung_main.common.Message.FAIL_CREATE_REPLY;

public class ChatBotException extends RuntimeException {
    public ChatBotException(){
        super(FAIL_CREATE_REPLY);
    }
}
