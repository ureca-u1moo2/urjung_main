package com.eureka.ip.team1.urjung_main.chatbot.utils;

import com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.LineAnalysisPrompts;
import com.eureka.ip.team1.urjung_main.chatbot.prompt.constants.PersonalAnalysisPrompts;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class PromptTemplateProvider {

    public static String buildPersonalValidationPrompt(String questionText) {
        return PersonalAnalysisPrompts.VALIDATION_PROMPT.formatted(questionText);
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

    public static String buildFinalAnalysisPrompt(UserDto userDto,
                                                        List<String> answers, List<String> questions, String plansJson) {
        int age = Period.between(userDto.getBirth(), LocalDate.now()).getYears();
        String gender = userDto.getGender();

        StringBuilder userAnswerSection = new StringBuilder();
        for (int i = 0; i < Math.min(questions.size(), answers.size()); i++) {
            userAnswerSection.append(String.format("%d. %s\n→ %s\n\n", i + 1, questions.get(i), answers.get(i)));
        }

        return PersonalAnalysisPrompts.FINAL_ANALYSIS_PROMPT.formatted(
                gender,
                age,
                userAnswerSection.toString().trim(),
                plansJson
        );
    }
}
