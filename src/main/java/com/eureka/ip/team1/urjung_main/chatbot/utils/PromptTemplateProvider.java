package com.eureka.ip.team1.urjung_main.chatbot.utils;

import com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.LineAnalysisPrompts;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.PersonalAnalysisPrompts;

public class PromptTemplateProvider {

    public static String buildPersonalValidationPrompt(String questionText) {
        return PersonalAnalysisPrompts.VALIDATION_PROMPT.formatted(questionText);
    }

    public static String buildFinalAnalysisPrompt(String a1, String a2, String a3, String plansJson) {
        return PersonalAnalysisPrompts.FINAL_ANALYSIS_PROMPT.formatted(a1, a2, a3, plansJson);
    }

    public static String buildAdditionalFeedbackValidationPrompt(String message) {
        return LineAnalysisPrompts.VALIDATION_PROMPT.formatted(message);
    }

    public static String buildFinalAnalysisByLinePrompt(String gender, int age,
                                                        String usageSummary, String planId,
                                                        String userMessage, String plansJson) {
        return LineAnalysisPrompts.FINAL_RECOMMENDATION_BY_LINE_PROMPT.formatted(
                gender, age, usageSummary, planId, userMessage, plansJson
        );
    }

    public static String buildFinalAnalysisPrompt(String gender, int age,
                                                        String usageSummary, String planId,
                                                        String userMessage, String plansJson) {
        return PersonalAnalysisPrompts.FINAL_ANALYSIS_PROMPT.formatted(
                gender, age, usageSummary, planId, userMessage, plansJson
        );
    }

    // 추후 RecommendationPrompts 등 확장 가능
}
