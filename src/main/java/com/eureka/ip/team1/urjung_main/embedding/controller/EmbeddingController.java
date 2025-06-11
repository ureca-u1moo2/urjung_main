package com.eureka.ip.team1.urjung_main.embedding.controller;

import com.eureka.ip.team1.urjung_main.embedding.service.EmbeddingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class EmbeddingController {

    private final EmbeddingServiceImpl embeddingServiceImpl;

    @PostMapping("/add")
    public ResponseEntity<?> addQuestion(@RequestParam String text) throws IOException {
        embeddingServiceImpl.indexWithEmbedding(text);
        return ResponseEntity.ok("Indexed");
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> search(@RequestParam String q) throws IOException {
        return ResponseEntity.ok(embeddingServiceImpl.searchSimilarQuestions(q));
    }

}

