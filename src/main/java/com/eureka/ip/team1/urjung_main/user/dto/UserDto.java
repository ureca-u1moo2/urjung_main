package com.eureka.ip.team1.urjung_main.user.dto;

import java.time.LocalDate;

import com.eureka.ip.team1.urjung_main.membership.entity.Membership;
import com.eureka.ip.team1.urjung_main.user.entity.User;

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
	
    public UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setGender(user.getGender());
        dto.setBirth(user.getBirth());
        dto.setMembership(user.getMembership());
        return dto;
    }
}
