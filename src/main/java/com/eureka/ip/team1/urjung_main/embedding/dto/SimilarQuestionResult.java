package com.eureka.ip.team1.urjung_main.embedding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarQuestionResult {
    private List<String> questions;
    private double topScore;
}
