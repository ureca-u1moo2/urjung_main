package com.eureka.ip.team1.urjung_main.chatbot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Part {
	
	private String text;
	
	// 팩토리 메소드
	public static Part createPart(String text) {
		Part part = new Part();
		part.text = text;
		
		return part;
	}
	
}
