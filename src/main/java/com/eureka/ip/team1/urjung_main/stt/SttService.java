package com.eureka.ip.team1.urjung_main.stt;

import org.springframework.web.multipart.MultipartFile;

public interface SttService {
    String transcribeWav(MultipartFile file);
}

