package com.eureka.ip.team1.urjung_main.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LineSubscriptionDto {
    private String planId;
    private String phoneNumber;
    private int discountedPrice;
}
