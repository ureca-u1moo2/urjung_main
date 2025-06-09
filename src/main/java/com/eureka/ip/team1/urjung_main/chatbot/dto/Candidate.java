package com.eureka.ip.team1.urjung_main.chatbot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Candidate {

	private Content content;
	
	// 팩토리 메소드
	public static Candidate createCandidate(Content content) {
		Candidate candidate = new Candidate();
		
		candidate.content = content;
		
		return candidate;
	}
}
