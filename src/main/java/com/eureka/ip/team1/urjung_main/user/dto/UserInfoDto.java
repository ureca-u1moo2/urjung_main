package com.eureka.ip.team1.urjung_main.user.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private String name;
    private String email;
    private String gender;
    private LocalDate birth;
    private String membershipName;
}
