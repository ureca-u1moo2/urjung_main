package com.eureka.ip.team1.urjung_main.user.dto;

import java.time.LocalDate;

import com.eureka.ip.team1.urjung_main.membership.entity.Membership;

import lombok.Data;

@Data
public class UserDto {
	private String userId;
	private String name;
	private String email;
	private String password;
	private String gender;
	private LocalDate birth;
	
	private Membership membership;
}
