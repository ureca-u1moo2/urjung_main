package com.eureka.ip.team1.urjung_main.log.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.eureka.ip.team1.urjung_main.log.dto.ChatLogDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ElasticsearchLogServiceImpl implements ElasticsearchLogService {

    private final ElasticsearchClient esClient;

    @Override
    public void saveChatLog(ChatLogDto dto) throws IOException {
        String docId = dto.getSessionId() + "_" + dto.getTimestamp().toEpochMilli();

        IndexResponse response = esClient.index(indexRequest -> indexRequest
                .index("chat_logs")
                .id(docId)
                .document(dto)
        );

        System.out.println("저장 완료: " + response.id());
    }
}
