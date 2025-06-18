package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PlanServiceTest {
    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanServiceImpl planService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    //요금제 목록 전체 조회 테스트
//    @Test
//    void getAllPlans_shouldReturnListOfPlanDto() {
//        // given
//        Plan plan1 = Plan.builder()
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
//        Plan plan2 = Plan.builder()
//                .id("uuid-2")
//                .name("Plan 2")
//                .price(50000)
//                .description("Description 2")
//                .dataAmount(20000L)
//                .callAmount(1000L)
//                .smsAmount(500L)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        when(planRepository.findAll()).thenReturn(Arrays.asList(plan1, plan2));
//
//        // when
//        List<PlanDto> plans = planService.getAllPlans();
//
//        // then
//        assertThat(plans).hasSize(2);
//        assertThat(plans.get(0).getId()).isEqualTo("uuid-1");
//        assertThat(plans.get(1).getId()).isEqualTo("uuid-2");
//    }

    // 요금제 목록 필터링 조회 테스트
    @Test
    @DisplayName("getPlansSorted should return list of PlanDto when sortBy is priceAsc")
    void getPlansSorted_shouldReturnPlanDtoList() {
        // given
        Plan plan1 = Plan.builder()
                .id("uuid-1")
                .name("Plan 1")
                .price(30000)
                .description("Description 1")
                .dataAmount(10000L)
                .callAmount(500L)
                .smsAmount(200L)
                .createdAt(LocalDateTime.now())
                .build();

        Plan plan2 = Plan.builder()
                .id("uuid-2")
                .name("Plan 2")
                .price(50000)
                .description("Description 2")
                .dataAmount(20000L)
                .callAmount(1000L)
                .smsAmount(500L)
                .createdAt(LocalDateTime.now())
                .build();

        when(planRepository.findAllByOrderByPriceAsc()).thenReturn(Arrays.asList(plan1, plan2));

        // when
        List<PlanDto> result = planService.getPlansSorted("priceAsc");

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("uuid-1");
        assertThat(result.get(1).getId()).isEqualTo("uuid-2");
    }

    @Test
    @DisplayName("getPlansSorted should return list of PlanDto when sortBy is priceDesc")
    void getPlansSorted_priceDesc_shouldReturnPlanDtoList() {
        // given
        Plan plan1 = Plan.builder().id("uuid-1").name("Plan 1").price(30000).description("Desc 1").dataAmount(10000L).callAmount(500L).smsAmount(200L).createdAt(LocalDateTime.now()).build();
        Plan plan2 = Plan.builder().id("uuid-2").name("Plan 2").price(50000).description("Desc 2").dataAmount(20000L).callAmount(1000L).smsAmount(500L).createdAt(LocalDateTime.now()).build();

        when(planRepository.findAllByOrderByPriceDesc()).thenReturn(Arrays.asList(plan1, plan2));

        // when
        List<PlanDto> result = planService.getPlansSorted("priceDesc");

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("uuid-1");
        assertThat(result.get(1).getId()).isEqualTo("uuid-2");
    }

    @Test
    @DisplayName("getPlansSorted should return list of PlanDto when sortBy is dataAsc")
    void getPlansSorted_dataAsc_shouldReturnPlanDtoList() {
        // given
        Plan plan1 = Plan.builder().id("uuid-1").name("Plan 1").price(30000).description("Desc 1").dataAmount(10000L).callAmount(500L).smsAmount(200L).createdAt(LocalDateTime.now()).build();
        Plan plan2 = Plan.builder().id("uuid-2").name("Plan 2").price(50000).description("Desc 2").dataAmount(20000L).callAmount(1000L).smsAmount(500L).createdAt(LocalDateTime.now()).build();

        when(planRepository.findAllByOrderByDataAmountAsc()).thenReturn(Arrays.asList(plan1, plan2));

        // when
        List<PlanDto> result = planService.getPlansSorted("dataAsc");

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("uuid-1");
        assertThat(result.get(1).getId()).isEqualTo("uuid-2");
    }

    @Test
    @DisplayName("getPlansSorted should return list of PlanDto when sortBy is dataDesc")
    void getPlansSorted_dataDesc_shouldReturnPlanDtoList() {
        // given
        Plan plan1 = Plan.builder().id("uuid-1").name("Plan 1").price(30000).description("Desc 1").dataAmount(10000L).callAmount(500L).smsAmount(200L).createdAt(LocalDateTime.now()).build();
        Plan plan2 = Plan.builder().id("uuid-2").name("Plan 2").price(50000).description("Desc 2").dataAmount(20000L).callAmount(1000L).smsAmount(500L).createdAt(LocalDateTime.now()).build();

//        when(planRepository.findAllByOrderByDataAmountDesc()).thenReturn(Arrays.asList(plan1, plan2));
        when(planRepository.findAll()).thenReturn(Arrays.asList(plan1, plan2));

        // when
        List<PlanDto> result = planService.getPlansSorted("dataDesc");

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("uuid-2");
        assertThat(result.get(1).getId()).isEqualTo("uuid-1");
    }

    @Test
    @DisplayName("getPlansSorted should return list of PlanDto when sortBy is popular")
    void getPlansSorted_popular_shouldReturnPlanDtoList() {
        // given
        Plan plan1 = Plan.builder().id("uuid-1").name("Plan 1").price(30000).description("Desc 1").dataAmount(10000L).callAmount(500L).smsAmount(200L).createdAt(LocalDateTime.now()).build();
        Plan plan2 = Plan.builder().id("uuid-2").name("Plan 2").price(50000).description("Desc 2").dataAmount(20000L).callAmount(1000L).smsAmount(500L).createdAt(LocalDateTime.now()).build();

        when(planRepository.findPopularPlans()).thenReturn(Arrays.asList("uuid-1", "uuid-2"));
        when(planRepository.findByIdsWithTags(Arrays.asList("uuid-1", "uuid-2")))
                .thenReturn(Arrays.asList(plan1, plan2));

        // when
        List<PlanDto> result = planService.getPlansSorted("popular");

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("uuid-1");
        assertThat(result.get(1).getId()).isEqualTo("uuid-2");
    }

    @Test
    @DisplayName("getPlansSorted should fallback to popular when sortBy is unknown")
    void getPlansSorted_unknownSortBy_shouldFallbackToPopular() {
        // given
        Plan plan1 = Plan.builder().id("uuid-1").name("Plan 1").price(30000).description("Desc 1").dataAmount(10000L).callAmount(500L).smsAmount(200L).createdAt(LocalDateTime.now()).build();
        Plan plan2 = Plan.builder().id("uuid-2").name("Plan 2").price(50000).description("Desc 2").dataAmount(20000L).callAmount(1000L).smsAmount(500L).createdAt(LocalDateTime.now()).build();

        when(planRepository.findPopularPlans()).thenReturn(Arrays.asList("uuid-1", "uuid-2"));
        when(planRepository.findByIdsWithTags(Arrays.asList("uuid-1", "uuid-2")))
                .thenReturn(Arrays.asList(plan1, plan2));

        // when
        List<PlanDto> result = planService.getPlansSorted("unknownSort");

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("uuid-1");
        assertThat(result.get(1).getId()).isEqualTo("uuid-2");
    }

    // 요금제 상세 페이지 조회
    @Test
    @DisplayName("getPlanDetail should return PlanDetailDto when plan exists")
    void getPlanDetail_shouldReturnPlanDetailDto() {
        // given
        String planId = "uuid-1";

        Plan plan = Plan.builder()
                .id(planId)
                .name("Plan 1")
                .price(30000)
                .description("Description 1")
                .dataAmount(10000L)
                .callAmount(500L)
                .smsAmount(200L)
                .createdAt(LocalDateTime.now())
                .build();

        when(planRepository.findById(planId)).thenReturn(java.util.Optional.of(plan));

        // when
        PlanDetailDto result = planService.getPlanDetail(planId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(planId);
        assertThat(result.getName()).isEqualTo("Plan 1");
        assertThat(result.getPrice()).isEqualTo(30000);
    }

    // 요금제 비교 테스트
    @Test
    @DisplayName("comparePlans should return list of PlanDetailDto when plans exist")
    void comparePlans_shouldReturnPlanDetailDtoList() {
        // given
        String planId1 = "uuid-1";
        String planId2 = "uuid-2";

        Plan plan1 = Plan.builder()
                .id(planId1)
                .name("Plan 1")
                .price(30000)
                .description("Description 1")
                .dataAmount(10000L)
                .callAmount(500L)
                .smsAmount(200L)
                .createdAt(LocalDateTime.now())
                .build();

        Plan plan2 = Plan.builder()
                .id(planId2)
                .name("Plan 2")
                .price(50000)
                .description("Description 2")
                .dataAmount(20000L)
                .callAmount(1000L)
                .smsAmount(500L)
                .createdAt(LocalDateTime.now())
                .build();

        when(planRepository.findAllById(Arrays.asList(planId1, planId2)))
                .thenReturn(Arrays.asList(plan1, plan2));

        // when
        List<PlanDetailDto> result = planService.comparePlans(Arrays.asList(planId1, planId2));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(planId1);
        assertThat(result.get(1).getId()).isEqualTo(planId2);
    }

    // 요금제 비교 예외 테스트
    @Test
    @DisplayName("comparePlans should throw NotFoundException when some plans not found")
    void comparePlans_shouldThrowNotFoundException_whenPlansMissing() {
        // given
        String planId1 = "uuid-1";
        String planId2 = "uuid-2";

        // findAllById 에서 하나만 리턴하도록 설정 (id2 없음 가정)
        when(planRepository.findAllById(Arrays.asList(planId1, planId2)))
                .thenReturn(Arrays.asList(
                        Plan.builder()
                                .id(planId1)
                                .name("Plan 1")
                                .price(30000)
                                .description("Description 1")
                                .dataAmount(10000L)
                                .callAmount(500L)
                                .smsAmount(200L)
                                .createdAt(LocalDateTime.now())
                                .build()
                ));

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(NotFoundException.class, () -> {
            planService.comparePlans(Arrays.asList(planId1, planId2));
        });
    }

}
