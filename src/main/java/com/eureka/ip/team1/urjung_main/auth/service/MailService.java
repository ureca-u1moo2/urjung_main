package com.eureka.ip.team1.urjung_main.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String email, String token) {
        try {
        	MimeMessage mimeMessage = mailSender.createMimeMessage();
        	
        	String subject = "비밀번호 재설정 안내";
            String resetUrl = "http://localhost:3000/find-password?token=" + token;
            String text = "아래 링크를 클릭해 비밀번호를 재설정하세요.\n" + resetUrl;

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text);
            mailSender.send(mimeMessage);     	
        } catch (MessagingException e) {
        	log.debug("Error sending email", e);
        }

    }
}
