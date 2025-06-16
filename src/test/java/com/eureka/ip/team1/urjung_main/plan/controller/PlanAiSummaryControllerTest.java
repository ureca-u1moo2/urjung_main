//package com.eureka.ip.team1.urjung_main.plan.controller;
//
//import com.eureka.ip.team1.urjung_main.common.ApiResponse;
//import com.eureka.ip.team1.urjung_main.common.enums.Result;
//import com.eureka.ip.team1.urjung_main.plan.dto.PlanAiSummaryResponseDto;
//import com.eureka.ip.team1.urjung_main.plan.service.PlanAiSummaryService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(PlanAiSummaryController.class)
//@WithMockUser(username = "testUser", roles = {"USER"})
//class PlanAiSummaryControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private PlanAiSummaryService planAiSummaryService;
//
//    @Test
//    @DisplayName("리뷰 요약 응답이 ApiResponse로 반환된다")
//    void getReviewSummary_returnsWrappedApiResponse() throws Exception {
//        // given
//        String planId = "test-plan";
//        PlanAiSummaryResponseDto dto = new PlanAiSummaryResponseDto("요약내용");
//
//        when(planAiSummaryService.summarizePlanReview(planId)).thenReturn(dto);
//
//        // when & then
//        mockMvc.perform(get("/api/plans/{planId}/reviews/summary", planId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
//                .andExpect(jsonPath("$.message").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.summary").value("요약내용"));
//    }
//}


package com.eureka.ip.team1.urjung_main.plan.controller;

import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanAiSummaryResponseDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanAiSummaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlanAiSummaryController.class)
@WithMockUser(username = "testUser", roles = {"USER"})
public class PlanAiSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanAiSummaryService planAiSummaryService;

    @Test
    @DisplayName("리뷰 요약 API 정상 호출")
    void getReviewSummary_success() throws Exception {
        // given
        PlanAiSummaryResponseDto responseDto = new PlanAiSummaryResponseDto("요약 내용");
        Mockito.when(planAiSummaryService.summarizePlanReview(anyString()))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/plans/plan-123/reviews/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("SUCCESS")))
                .andExpect(jsonPath("$.data.summary", is("요약 내용")));
    }
}
