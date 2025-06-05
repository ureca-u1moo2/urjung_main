package com.eureka.ip.team1.urjung_main.chatbot;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Content {

	private String role;
	
	private List<Part> parts;
	
	// 팩토리 메소드
	public static Content createContent(String role, List<Part> parts) {
		Content content = new Content();
		
		content.role = role;
		content.parts = parts;
		
		return content;
	}
	
}
