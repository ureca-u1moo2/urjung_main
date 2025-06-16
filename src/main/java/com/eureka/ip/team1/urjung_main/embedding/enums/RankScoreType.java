package com.eureka.ip.team1.urjung_main.embedding.enums;

public enum RankScoreType {
    DATA_HEAVY("rank_scores.data_heavy"),
    CALL_HEAVY("rank_scores.call_heavy"),
    LOW_PRICE("rank_scores.low_price"),
    SENIOR_FRIENDLY("rank_scores.senior_friendly"),
    STUDENT_FRIENDLY("rank_scores.student_friendly"),
    STREAMING("rank_scores.streaming"),
    ROAMING("rank_scores.roaming");

    private final String fieldPath;

    RankScoreType(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public String field() {
        return fieldPath;
    }
}

