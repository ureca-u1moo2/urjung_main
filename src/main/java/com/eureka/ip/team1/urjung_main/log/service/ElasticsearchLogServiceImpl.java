package com.eureka.ip.team1.urjung_main.log.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.eureka.ip.team1.urjung_main.log.dto.ChatLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ElasticsearchLogServiceImpl implements ElasticsearchLogService{

    private final ElasticsearchClient esClient;

    @Override
    public void saveChatLog(ChatLogDto dto) throws IOException {
        // Elasticsearch는 각 문서를 _id 값으로 구분하기 때문에 세션 단위 + 시간으로 설정
        String docId = dto.getSessionId() + "_" + dto.getTimestamp().toEpochMilli();

        IndexResponse response = esClient.index(indexRequest -> indexRequest
                        .index("chat_logs")
                        .id(docId)
                        .document(dto)
        );

        System.out.println("저장 완료: " + response.id());
    }
}
