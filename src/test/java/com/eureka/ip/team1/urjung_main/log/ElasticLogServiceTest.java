package com.eureka.ip.team1.urjung_main.log;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.eureka.ip.team1.urjung_main.chatbot.enums.Topic;
import com.eureka.ip.team1.urjung_main.log.dto.ChatLogDto;
import com.eureka.ip.team1.urjung_main.log.service.ElasticsearchLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ElasticLogServiceTest {

    @Mock
    private ElasticsearchClient esClient;

    @InjectMocks
    private ElasticsearchLogServiceImpl logService;

    @Test
    void testSaveChatLog() throws IOException {
        // given
        Instant timestamp = Instant.parse("2025-06-11T08:38:56.171140100Z");

        ChatLogDto dto = new ChatLogDto(
                "1",
                null,
                timestamp,
                "아 WebClient로 리팩토링 해야겠네 내일",
                Topic.ETC,
                "WebClient로 리팩토링하신다니, 개발에 열정이 넘치시네요! 🔥",
                null, // 아직 미구현
                null, // 아직 미구현
                2527L
        );

        IndexResponse mockResponse = mock(IndexResponse.class);
        when(mockResponse.id()).thenReturn("null_" + timestamp.toEpochMilli());

        // mock ESClient 동작
        when(esClient.index(any(Function.class))).thenReturn(mockResponse);

        logService = new ElasticsearchLogServiceImpl(esClient);

        // when
        logService.saveChatLog(dto);

        // then
        verify(esClient, times(1)).index(any(Function.class));
    }

}
