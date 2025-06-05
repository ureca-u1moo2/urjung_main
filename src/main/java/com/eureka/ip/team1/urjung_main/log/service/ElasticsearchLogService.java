package com.eureka.ip.team1.urjung_main.log.service;

import com.eureka.ip.team1.urjung_main.log.dto.ChatLogDto;

import java.io.IOException;

public interface ElasticsearchLogService {
    void saveChatLog(ChatLogDto dto) throws IOException;
}
