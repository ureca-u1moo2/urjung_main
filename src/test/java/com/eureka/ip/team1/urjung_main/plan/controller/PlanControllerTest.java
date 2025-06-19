package com.eureka.ip.team1.urjung_main.plan.controller;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.service.PlanService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = PlanController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
//@WebMvcTest(PlanController.class)
public class PlanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanService planService;

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
//    @DisplayName("요금제 목록 조회 성공")
//    void getPlans_success() throws Exception {
//        // given
//        PlanDto plan1 = PlanDto.builder()
//                .id("uuid-1")
//                .name("Plan 1")
//                .price(30000)
//                .description("Description 1")
//                .dataAmount(10000L)
//                .callAmount(500L)
//                .smsAmount(200L)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        when(planService.getAllPlans()).thenReturn(Arrays.asList(plan1));
//
//        // when & then
//        mockMvc.perform(get("/api/plans"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result").value("SUCCESS"))
//                .andExpect(jsonPath("$.data[0].id").value("uuid-1"))
//                .andExpect(jsonPath("$.data[0].name").value("Plan 1"));
//    }
//
//    @Test
//    @DisplayName("요금제 목록 조회 - 빈 리스트 반환")
//    void getPlans_emptyList() throws Exception {
//        // given
//        when(planService.getAllPlans()).thenReturn(Collections.emptyList());
//
//        // when & then
//        mockMvc.perform(get("/api/plans"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result").value("SUCCESS"))
//                .andExpect(jsonPath("$.data").isEmpty());
//    }

    // 요금제 목록 조회 조건

//    @Test
//    @DisplayName("요금제 목록 조회 성공 (정렬 파라미터 popular)")
//    void getPlansSorted_success() throws Exception {
//        // given
//        PlanDto planDto = PlanDto.builder()
//                .id("id1")
//                .name("Plan A")
//                .price(30000)
//                .description("Description 1")
//                .dataAmount(10000L)
//                .callAmount(500L)
//                .smsAmount(200L)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        List<PlanDto> content = List.of(planDto);
//        Page<PlanDto> pageResult = new PageImpl<>(content, PageRequest.of(0, 10), 1);
//
//        when(planService.getPlansSorted("popular", 0, 10)).thenReturn(pageResult);
//
//        // when & then
//        mockMvc.perform(get("/api/plans")
//                        .param("sortBy", "popular")
//                        .param("page", "0")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.content[0].id").value("id1"))
//                .andExpect(jsonPath("$.data.content[0].name").value("Plan A"));
//    }

//    @Test
//    @DisplayName("요금제 목록 조회 - 빈 리스트 반환 (정렬 파라미터 price)")
//    void getPlans_emptyList() throws Exception {
//        // given
//        when(planService.getPlansSorted("price")).thenReturn(Collections.emptyList());
//
//        // when & then
//        mockMvc.perform(get("/api/plans").param("sortBy", "price"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result").value("SUCCESS"))
//                .andExpect(jsonPath("$.data").isEmpty());
//    }
@Test
@DisplayName("요금제 목록 조회 - /filter 경로, 페이징 적용")
void getPlansSorted_withPaging_filterEndpoint_success() throws Exception {
    // given
    PlanDto plan = PlanDto.builder()
            .id("plan-001")
            .name("기본 요금제")
            .price(10000)
            .dataAmount(10L)
            .callAmount(100L)
            .smsAmount(100L)
            .build();

    Page<PlanDto> page = new PageImpl<>(List.of(plan), PageRequest.of(0, 10), 1);

    Mockito.when(planService.getPlansSorted("popular", 0, 10)).thenReturn(page);

    // when & then
    mockMvc.perform(get("/api/plans/filter")
                    .param("sortBy", "popular")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content[0].id").value("plan-001"))
            .andExpect(jsonPath("$.data.content[0].name").value("기본 요금제"))
            .andExpect(jsonPath("$.data.content[0].price").value(10000));
}

    @Test
    @DisplayName("요금제 목록 조회 - 페이징 없이")
    void getPlansSorted_withoutPaging_success() throws Exception {
        PlanDto plan = PlanDto.builder()
                .id("plan-001")
                .name("기본 요금제")
                .price(10000)
                .dataAmount(10L)
                .callAmount(100L)
                .smsAmount(100L)
                .build();

        Mockito.when(planService.getPlansSorted("popular")).thenReturn(List.of(plan));

        mockMvc.perform(get("/api/plans")
                        .param("sortBy", "popular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].id").value("plan-001"))
                .andExpect(jsonPath("$.data[0].name").value("기본 요금제"))
                .andExpect(jsonPath("$.data[0].price").value(10000));
    }

    @Test
    @DisplayName("요금제 상세 조회 성공")
    void getPlanDetail_success() throws Exception {
        // given
//        String planId = "uuid-1";

        PlanDetailDto planDetail = PlanDetailDto.builder()
                .id("id1")
                .name("Plan A")
                .price(30000)
                .description("Description 1")
                .dataAmount(10000L)
                .callAmount(500L)
                .smsAmount(200L)
                .createdAt(LocalDateTime.now())
                .build();

        when(planService.getPlanDetail("id1")).thenReturn(planDetail);

        // when & then
        mockMvc.perform(get("/api/plans/id1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value("id1"))
                .andExpect(jsonPath("$.data.name").value("Plan A"))
                .andExpect(jsonPath("$.data.price").value(30000));
    }

    @Test
    @DisplayName("요금제 비교 조회 성공")
    void comparePlans_success() throws Exception {
        // given
        PlanDetailDto plan1 = PlanDetailDto.builder()
                .id("uuid-1")
                .name("Plan 1")
                .price(30000)
                .description("Description 1")
                .dataAmount(10000L)
                .callAmount(500L)
                .smsAmount(200L)
                .createdAt(LocalDateTime.now())
                .build();

        PlanDetailDto plan2 = PlanDetailDto.builder()
                .id("uuid-2")
                .name("Plan 2")
                .price(50000)
                .description("Description 2")
                .dataAmount(20000L)
                .callAmount(1000L)
                .smsAmount(500L)
                .createdAt(LocalDateTime.now())
                .build();

        when(planService.comparePlans(Arrays.asList("uuid-1", "uuid-2")))
                .thenReturn(Arrays.asList(plan1, plan2));

        // when & then
        mockMvc.perform(get("/api/plans/compare")
                        .param("planIds", "uuid-1,uuid-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].id").value("uuid-1"))
                .andExpect(jsonPath("$.data[1].id").value("uuid-2"));
    }


}
