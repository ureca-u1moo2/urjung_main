package com.eureka.ip.team1.urjung_main.stt.service;

import org.springframework.web.multipart.MultipartFile;

public interface SttService {
    String transcribeWav(MultipartFile file);
}

