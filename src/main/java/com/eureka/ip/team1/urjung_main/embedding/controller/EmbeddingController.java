package com.eureka.ip.team1.urjung_main.embedding.controller;

import com.eureka.ip.team1.urjung_main.embedding.service.EmbeddingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class EmbeddingController {

    private final EmbeddingServiceImpl embeddingServiceImpl;


        @PostMapping("/add")
        public Mono<ResponseEntity<String>> addQuestion(@RequestParam String text) {
            return embeddingServiceImpl.indexWithEmbedding(text)
                    .thenReturn(ResponseEntity.ok("Indexed"));
        }

        @GetMapping("/search")
        public Mono<ResponseEntity<List<String>>> search(@RequestParam String q) {
                return embeddingServiceImpl.searchSimilarQuestions(q)
                        .map(ResponseEntity::ok);
        }

}

