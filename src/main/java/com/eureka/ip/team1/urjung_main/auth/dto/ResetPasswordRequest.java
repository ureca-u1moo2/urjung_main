package com.eureka.ip.team1.urjung_main.auth.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
	private String token;
	private String newPassword;
}
