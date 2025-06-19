//package com.eureka.ip.team1.urjung_main.plan.service;
//
//import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
//import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
//import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
//import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
//import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//public class PlanServiceTest {
//    @Mock
//    private PlanRepository planRepository;
//
//    @InjectMocks
//    private PlanServiceImpl planService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
////    //요금제 목록 전체 조회 테스트
////    @Test
////    void getAllPlans_shouldReturnListOfPlanDto() {
////        // given
////        Plan plan1 = Plan.builder()
////                .id("uuid-1")
////                .name("Plan 1")
////                .price(30000)
////                .description("Description 1")
////                .dataAmount(10000L)
////                .callAmount(500L)
////                .smsAmount(200L)
////                .createdAt(LocalDateTime.now())
////                .build();
////
////        Plan plan2 = Plan.builder()
////                .id("uuid-2")
////                .name("Plan 2")
////                .price(50000)
////                .description("Description 2")
////                .dataAmount(20000L)
////                .callAmount(1000L)
////                .smsAmount(500L)
////                .createdAt(LocalDateTime.now())
////                .build();
////
////        when(planRepository.findAll()).thenReturn(Arrays.asList(plan1, plan2));
////
////        // when
////        List<PlanDto> plans = planService.getAllPlans();
////
////        // then
////        assertThat(plans).hasSize(2);
////        assertThat(plans.get(0).getId()).isEqualTo("uuid-1");
////        assertThat(plans.get(1).getId()).isEqualTo("uuid-2");
////    }
//
//    // 요금제 목록 필터링 조회 테스트
//    @Test
//    @DisplayName("getPlansSorted should return list of PlanDto when sortBy is priceAsc")
//    void getPlansSorted_shouldReturnPlanDtoList() {
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
//        when(planRepository.findAllByOrderByPriceAsc()).thenReturn(Arrays.asList(plan1, plan2));
//
//        // when
//        List<PlanDto> result = planService.getPlansSorted("priceAsc");
//
//        // then
//        assertThat(result).hasSize(2);
//        assertThat(result.get(0).getId()).isEqualTo("uuid-1");
//        assertThat(result.get(1).getId()).isEqualTo("uuid-2");
//    }
//
//    @Test
//    @DisplayName("getPlansSorted should return list of PlanDto when sortBy is priceDesc")
//    void getPlansSorted_priceDesc_shouldReturnPlanDtoList() {
//        // given
//        Plan plan1 = Plan.builder().id("uuid-1").name("Plan 1").price(30000).description("Desc 1").dataAmount(10000L).callAmount(500L).smsAmount(200L).createdAt(LocalDateTime.now()).build();
//        Plan plan2 = Plan.builder().id("uuid-2").name("Plan 2").price(50000).description("Desc 2").dataAmount(20000L).callAmount(1000L).smsAmount(500L).createdAt(LocalDateTime.now()).build();
//
//        when(planRepository.findAllByOrderByPriceDesc()).thenReturn(Arrays.asList(plan1, plan2));
//
//        // when
//        List<PlanDto> result = planService.getPlansSorted("priceDesc");
//
//        // then
//        assertThat(result).hasSize(2);
//        assertThat(result.get(0).getId()).isEqualTo("uuid-1");
//        assertThat(result.get(1).getId()).isEqualTo("uuid-2");
//    }
//
//    @Test
//    @DisplayName("getPlansSorted should return list of PlanDto when sortBy is dataAsc")
//    void getPlansSorted_dataAsc_shouldReturnPlanDtoList() {
//        // given
//        Plan plan1 = Plan.builder().id("uuid-1").name("Plan 1").price(30000).description("Desc 1").dataAmount(10000L).callAmount(500L).smsAmount(200L).createdAt(LocalDateTime.now()).build();
//        Plan plan2 = Plan.builder().id("uuid-2").name("Plan 2").price(50000).description("Desc 2").dataAmount(20000L).callAmount(1000L).smsAmount(500L).createdAt(LocalDateTime.now()).build();
//
//        when(planRepository.findAllByOrderByDataAmountAsc()).thenReturn(Arrays.asList(plan1, plan2));
//
//        // when
//        List<PlanDto> result = planService.getPlansSorted("dataAsc");
//
//        // then
//        assertThat(result).hasSize(2);
//        assertThat(result.get(0).getId()).isEqualTo("uuid-1");
//        assertThat(result.get(1).getId()).isEqualTo("uuid-2");
//    }
//
//    @Test
//    @DisplayName("getPlansSorted should return list of PlanDto when sortBy is dataDesc")
//    void getPlansSorted_dataDesc_shouldReturnPlanDtoList() {
//        // given
//        Plan plan1 = Plan.builder().id("uuid-1").name("Plan 1").price(30000).description("Desc 1").dataAmount(10000L).callAmount(500L).smsAmount(200L).createdAt(LocalDateTime.now()).build();
//        Plan plan2 = Plan.builder().id("uuid-2").name("Plan 2").price(50000).description("Desc 2").dataAmount(20000L).callAmount(1000L).smsAmount(500L).createdAt(LocalDateTime.now()).build();
//
////        when(planRepository.findAllByOrderByDataAmountDesc()).thenReturn(Arrays.asList(plan1, plan2));
//        when(planRepository.findAll()).thenReturn(Arrays.asList(plan1, plan2));
//
//        // when
//        List<PlanDto> result = planService.getPlansSorted("dataDesc");
//
//        // then
//        assertThat(result).hasSize(2);
//        assertThat(result.get(0).getId()).isEqualTo("uuid-2");
//        assertThat(result.get(1).getId()).isEqualTo("uuid-1");
//    }
//
//    @Test
//    @DisplayName("getPlansSorted should return list of PlanDto when sortBy is popular")
//    void getPlansSorted_popular_shouldReturnPlanDtoList() {
//        // given
//        Plan plan1 = Plan.builder().id("uuid-1").name("Plan 1").price(30000).description("Desc 1").dataAmount(10000L).callAmount(500L).smsAmount(200L).createdAt(LocalDateTime.now()).build();
//        Plan plan2 = Plan.builder().id("uuid-2").name("Plan 2").price(50000).description("Desc 2").dataAmount(20000L).callAmount(1000L).smsAmount(500L).createdAt(LocalDateTime.now()).build();
//
//        when(planRepository.findPopularPlans()).thenReturn(Arrays.asList("uuid-1", "uuid-2"));
//        when(planRepository.findByIdsWithTags(Arrays.asList("uuid-1", "uuid-2")))
//                .thenReturn(Arrays.asList(plan1, plan2));
//
//        // when
//        List<PlanDto> result = planService.getPlansSorted("popular");
//
//        // then
//        assertThat(result).hasSize(2);
//        assertThat(result.get(0).getId()).isEqualTo("uuid-1");
//        assertThat(result.get(1).getId()).isEqualTo("uuid-2");
//    }
//
//    @Test
//    @DisplayName("getPlansSorted should fallback to popular when sortBy is unknown")
//    void getPlansSorted_unknownSortBy_shouldFallbackToPopular() {
//        // given
//        Plan plan1 = Plan.builder().id("uuid-1").name("Plan 1").price(30000).description("Desc 1").dataAmount(10000L).callAmount(500L).smsAmount(200L).createdAt(LocalDateTime.now()).build();
//        Plan plan2 = Plan.builder().id("uuid-2").name("Plan 2").price(50000).description("Desc 2").dataAmount(20000L).callAmount(1000L).smsAmount(500L).createdAt(LocalDateTime.now()).build();
//
//        when(planRepository.findPopularPlans()).thenReturn(Arrays.asList("uuid-1", "uuid-2"));
//        when(planRepository.findByIdsWithTags(Arrays.asList("uuid-1", "uuid-2")))
//                .thenReturn(Arrays.asList(plan1, plan2));
//
//        // when
//        List<PlanDto> result = planService.getPlansSorted("unknownSort");
//
//        // then
//        assertThat(result).hasSize(2);
//        assertThat(result.get(0).getId()).isEqualTo("uuid-1");
//        assertThat(result.get(1).getId()).isEqualTo("uuid-2");
//    }
//
//    // 요금제 상세 페이지 조회
//    @Test
//    @DisplayName("getPlanDetail should return PlanDetailDto when plan exists")
//    void getPlanDetail_shouldReturnPlanDetailDto() {
//        // given
//        String planId = "uuid-1";
//
//        Plan plan = Plan.builder()
//                .id(planId)
//                .name("Plan 1")
//                .price(30000)
//                .description("Description 1")
//                .dataAmount(10000L)
//                .callAmount(500L)
//                .smsAmount(200L)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        when(planRepository.findById(planId)).thenReturn(java.util.Optional.of(plan));
//
//        // when
//        PlanDetailDto result = planService.getPlanDetail(planId);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getId()).isEqualTo(planId);
//        assertThat(result.getName()).isEqualTo("Plan 1");
//        assertThat(result.getPrice()).isEqualTo(30000);
//    }
//
//    // 요금제 비교 테스트
//    @Test
//    @DisplayName("comparePlans should return list of PlanDetailDto when plans exist")
//    void comparePlans_shouldReturnPlanDetailDtoList() {
//        // given
//        String planId1 = "uuid-1";
//        String planId2 = "uuid-2";
//
//        Plan plan1 = Plan.builder()
//                .id(planId1)
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
//                .id(planId2)
//                .name("Plan 2")
//                .price(50000)
//                .description("Description 2")
//                .dataAmount(20000L)
//                .callAmount(1000L)
//                .smsAmount(500L)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        when(planRepository.findAllById(Arrays.asList(planId1, planId2)))
//                .thenReturn(Arrays.asList(plan1, plan2));
//
//        // when
//        List<PlanDetailDto> result = planService.comparePlans(Arrays.asList(planId1, planId2));
//
//        // then
//        assertThat(result).hasSize(2);
//        assertThat(result.get(0).getId()).isEqualTo(planId1);
//        assertThat(result.get(1).getId()).isEqualTo(planId2);
//    }
//
//    // 요금제 비교 예외 테스트
//    @Test
//    @DisplayName("comparePlans should throw NotFoundException when some plans not found")
//    void comparePlans_shouldThrowNotFoundException_whenPlansMissing() {
//        // given
//        String planId1 = "uuid-1";
//        String planId2 = "uuid-2";
//
//        // findAllById 에서 하나만 리턴하도록 설정 (id2 없음 가정)
//        when(planRepository.findAllById(Arrays.asList(planId1, planId2)))
//                .thenReturn(Arrays.asList(
//                        Plan.builder()
//                                .id(planId1)
//                                .name("Plan 1")
//                                .price(30000)
//                                .description("Description 1")
//                                .dataAmount(10000L)
//                                .callAmount(500L)
//                                .smsAmount(200L)
//                                .createdAt(LocalDateTime.now())
//                                .build()
//                ));
//
//        // when & then
//        org.junit.jupiter.api.Assertions.assertThrows(NotFoundException.class, () -> {
//            planService.comparePlans(Arrays.asList(planId1, planId2));
//        });
//    }
//
//}
//package com.eureka.ip.team1.urjung_main.plan.service;
//
//import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
//import com.eureka.ip.team1.urjung_main.plan.dto.PlanDetailDto;
//import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
//import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
//import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.data.domain.*;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class PlanServiceTest {
//
//    private PlanRepository planRepository;
//    private PlanServiceImpl planService;
//
//    @BeforeEach
//    void setUp() {
//        planRepository = mock(PlanRepository.class);
//        planService = new PlanServiceImpl(planRepository);
//    }
//
//    @Test
//    @DisplayName("정렬: priceAsc - 목록 조회")
//    void getPlansSorted_priceAsc() {
//        List<Plan> plans = List.of(Plan.builder().id("1").name("Plan A").price(10000).build());
//        when(planRepository.findAll(Sort.by(Sort.Direction.ASC, "price"))).thenReturn(plans);
//
//        List<PlanDto> result = planService.getPlansSorted("priceAsc");
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).getPrice()).isEqualTo(10000);
//    }
//
//    @Test
//    @DisplayName("정렬: 잘못된 sortBy 값 → popular fallback")
//    void getPlansSorted_invalidSort_shouldFallbackToPopular() {
//        Plan plan = Plan.builder().id("fallback").name("Popular").build();
//        when(planRepository.findPopularPlans()).thenReturn(List.of("fallback"));
//        when(planRepository.findByIdsWithTags(List.of("fallback"))).thenReturn(List.of(plan));
//
//        List<PlanDto> result = planService.getPlansSorted("invalid-key");
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).getName()).isEqualTo("Popular");
//    }
//
//
//    @Test
//    @DisplayName("페이징 정렬: priceDesc")
//    void getPlansSorted_priceDesc_page() {
//        Plan plan = Plan.builder().id("1").name("Plan A").price(20000).build();
//        Page<Plan> planPage = new PageImpl<>(List.of(plan));
//        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "price"));
//        when(planRepository.findAll(pageable)).thenReturn(planPage);
//
//        Page<PlanDto> result = planService.getPlansSorted("priceDesc", 0, 10);
//        assertThat(result.getContent().get(0).getPrice()).isEqualTo(20000);
//    }
//
//    @Test
//    @DisplayName("페이징 정렬: priceAsc")
//    void getPlansSorted_priceAsc_page() {
//        Plan plan = Plan.builder().id("1").name("A").price(15000).build();
//        Page<Plan> page = new PageImpl<>(List.of(plan));
//        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "price"));
//
//        when(planRepository.findAll(pageable)).thenReturn(page);
//
//        Page<PlanDto> result = planService.getPlansSorted("priceAsc", 0, 10);
//        assertThat(result.getContent().get(0).getPrice()).isEqualTo(15000);
//    }
//
//
//    @Test
//    @DisplayName("데이터 무제한 정렬 dataDesc")
//    void getPlansSorted_dataDesc() {
//        Plan p1 = Plan.builder().id("1").name("A").dataAmount(-1L).build();
//        Plan p2 = Plan.builder().id("2").name("B").dataAmount(1000L).build();
//        when(planRepository.findAll()).thenReturn(List.of(p1, p2));
//
//        List<PlanDto> result = planService.getPlansSorted("dataDesc");
//        assertThat(result.get(0).getDataAmount()).isEqualTo(-1L); // 무제한이 먼저 나와야 함
//    }
//
//    @Test
//    @DisplayName("dataDesc 페이징")
//    void getPlansSorted_dataDesc_paging() {
//        Plan p1 = Plan.builder().id("1").name("A").dataAmount(-1L).build();
//        Plan p2 = Plan.builder().id("2").name("B").dataAmount(500L).build();
//        when(planRepository.findAll()).thenReturn(List.of(p1, p2));
//
//        Page<PlanDto> result = planService.getPlansSorted("dataDesc", 0, 1);
//        assertThat(result.getContent()).hasSize(1);
//    }
//
//    @Test
//    @DisplayName("페이징 정렬: dataAsc")
//    void getPlansSorted_dataAsc_page() {
//        Plan plan = Plan.builder().id("1").name("A").dataAmount(1000L).build();
//        Page<Plan> page = new PageImpl<>(List.of(plan));
//        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "dataAmount"));
//
//        when(planRepository.findAll(pageable)).thenReturn(page);
//
//        Page<PlanDto> result = planService.getPlansSorted("dataAsc", 0, 10);
//        assertThat(result.getContent().get(0).getDataAmount()).isEqualTo(1000L);
//    }
//
//    @Test
//    @DisplayName("페이징 정렬: 잘못된 sortBy 값 → popular fallback")
//    void getPlansSorted_invalidSort_page_shouldFallbackToPopular() {
//        Plan plan = Plan.builder().id("fallback").name("Popular").build();
//        when(planRepository.findPopularPlans()).thenReturn(List.of("fallback"));
//        when(planRepository.findByIdsWithTags(List.of("fallback"))).thenReturn(List.of(plan));
//
//        Page<PlanDto> result = planService.getPlansSorted("invalid-key", 0, 10);
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getName()).isEqualTo("Popular");
//    }
//
//
//
//    @Test
//    @DisplayName("인기순 정렬: popular")
//    void getPlansSorted_popular() {
//        Plan p = Plan.builder().id("1").name("Popular").build();
//        when(planRepository.findPopularPlans()).thenReturn(List.of("1"));
//        when(planRepository.findByIdsWithTags(List.of("1"))).thenReturn(List.of(p));
//
//        List<PlanDto> result = planService.getPlansSorted("popular");
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).getName()).isEqualTo("Popular");
//    }
//
//    @Test
//    @DisplayName("popular 페이징")
//    void getPlansSorted_popular_paging() {
//        Plan p = Plan.builder().id("1").name("Popular").build();
//        when(planRepository.findPopularPlans()).thenReturn(List.of("1"));
//        when(planRepository.findByIdsWithTags(List.of("1"))).thenReturn(List.of(p));
//
//        Page<PlanDto> result = planService.getPlansSorted("popular", 0, 5);
//        assertThat(result.getContent()).hasSize(1);
//    }
//
//    @Test
//    @DisplayName("요금제 상세 조회 성공")
//    void getPlanDetail_success() {
//        Plan plan = Plan.builder().id("1").name("Detail").build();
//        when(planRepository.findById("1")).thenReturn(Optional.of(plan));
//
//        PlanDetailDto result = planService.getPlanDetail("1");
//        assertThat(result.getName()).isEqualTo("Detail");
//    }
//
//    @Test
//    @DisplayName("요금제 상세 조회 실패 - 예외 발생")
//    void getPlanDetail_fail() {
//        when(planRepository.findById("x")).thenReturn(Optional.empty());
//        assertThatThrownBy(() -> planService.getPlanDetail("x"))
//                .isInstanceOf(NotFoundException.class);
//    }
//
//    @Test
//    @DisplayName("요금제 비교 성공")
//    void comparePlans_success() {
//        Plan plan = Plan.builder().id("1").name("Compare").build();
//        when(planRepository.findAllById(List.of("1"))).thenReturn(List.of(plan));
//
//        List<PlanDetailDto> result = planService.comparePlans(List.of("1"));
//        assertThat(result).hasSize(1);
//    }
//
//    @Test
//    @DisplayName("요금제 비교 실패 - 일부 없음")
//    void comparePlans_fail() {
//        Plan plan = Plan.builder().id("1").name("Compare").build();
//        when(planRepository.findAllById(List.of("1", "2"))).thenReturn(List.of(plan));
//
//        assertThatThrownBy(() -> planService.comparePlans(List.of("1", "2")))
//                .isInstanceOf(NotFoundException.class);
//    }
//
//    @Test
//    @DisplayName("dataDesc: dataAmount == -1, null, 일반값 모두 섞인 경우 정렬 확인")
//    void getPlansSorted_dataDesc_fullBranchCoverage() {
//        Plan unlimited = Plan.builder().id("1").name("무제한").dataAmount(-1L).build();
//        Plan normal = Plan.builder().id("2").name("일반").dataAmount(500L).build();
//        Plan unknown = Plan.builder().id("3").name("모름").dataAmount(null).build();
//
//        when(planRepository.findAll()).thenReturn(List.of(normal, unknown, unlimited));
//
//        List<PlanDto> result = planService.getPlansSorted("dataDesc");
//
//        // 정렬 순서: 무제한(-1) → 일반(500) → null
//        assertThat(result.get(0).getId()).isEqualTo("1");
//        assertThat(result.get(1).getId()).isEqualTo("2");
//        assertThat(result.get(2).getId()).isEqualTo("3");
//    }
//
//}

package com.eureka.ip.team1.urjung_main.plan.service;

import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
import com.eureka.ip.team1.urjung_main.plan.dto.PlanDto;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import com.eureka.ip.team1.urjung_main.plan.service.PlanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlanServiceTest {

    private PlanRepository planRepository;
    private PlanServiceImpl planService;

    @BeforeEach
    void setUp() {
        planRepository = mock(PlanRepository.class);
        planService = new PlanServiceImpl(planRepository);
    }

    @Test
    @DisplayName("priceAsc 정렬")
    void getPlansSorted_priceAsc() {
        Plan p = Plan.builder().id("p1").price(10000).build();
        when(planRepository.findAll(Sort.by(Sort.Direction.ASC, "price")))
                .thenReturn(List.of(p));
        List<PlanDto> result = planService.getPlansSorted("priceAsc");
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("priceDesc 정렬")
    void getPlansSorted_priceDesc() {
        Plan p = Plan.builder().id("p1").price(30000).build();
        when(planRepository.findAll(Sort.by(Sort.Direction.DESC, "price")))
                .thenReturn(List.of(p));
        List<PlanDto> result = planService.getPlansSorted("priceDesc");
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("dataAsc 정렬")
    void getPlansSorted_dataAsc() {
        Plan p = Plan.builder().id("p1").dataAmount(1000L).build();
        when(planRepository.findAll(Sort.by(Sort.Direction.ASC, "dataAmount")))
                .thenReturn(List.of(p));
        List<PlanDto> result = planService.getPlansSorted("dataAsc");
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("dataDesc 정렬: -1, null, 일반값 모두 포함")
    void getPlansSorted_dataDesc_withVariants() {
        Plan p1 = Plan.builder().id("1").dataAmount(-1L).build(); // 무제한
        Plan p2 = Plan.builder().id("2").dataAmount(500L).build(); // 일반
        Plan p3 = Plan.builder().id("3").dataAmount(null).build(); // null
        when(planRepository.findAll()).thenReturn(List.of(p2, p3, p1));
        List<PlanDto> result = planService.getPlansSorted("dataDesc");
        assertThat(result.get(0).getId()).isEqualTo("1");
    }

    @Test
    @DisplayName("popular 정렬")
    void getPlansSorted_popular() {
        Plan p = Plan.builder().id("1").name("Popular").build();
        when(planRepository.findPopularPlans()).thenReturn(List.of("1"));
        when(planRepository.findByIdsWithTags(List.of("1"))).thenReturn(List.of(p));
        List<PlanDto> result = planService.getPlansSorted("popular");
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("unknown 정렬 → popular fallback")
    void getPlansSorted_unknown() {
        Plan p = Plan.builder().id("1").name("Popular").build();
        when(planRepository.findPopularPlans()).thenReturn(List.of("1"));
        when(planRepository.findByIdsWithTags(List.of("1"))).thenReturn(List.of(p));
        List<PlanDto> result = planService.getPlansSorted("xyz");
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("페이징: priceAsc")
    void getPlansSorted_page_priceAsc() {
        Plan p = Plan.builder().id("p1").price(10000).build();
        Page<Plan> page = new PageImpl<>(List.of(p));
        when(planRepository.findAll(any(Pageable.class))).thenReturn(page);
        Page<PlanDto> result = planService.getPlansSorted("priceAsc", 0, 10);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("페이징: dataDesc")
    void getPlansSorted_page_dataDesc() {
        Plan p1 = Plan.builder().id("p1").dataAmount(-1L).build();
        Plan p2 = Plan.builder().id("p2").dataAmount(500L).build();
        when(planRepository.findAll()).thenReturn(List.of(p1, p2));
        Page<PlanDto> result = planService.getPlansSorted("dataDesc", 0, 1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("p1");
    }

    @Test
    @DisplayName("페이징: popular")
    void getPlansSorted_page_popular() {
        Plan p = Plan.builder().id("1").build();
        when(planRepository.findPopularPlans()).thenReturn(List.of("1"));
        when(planRepository.findByIdsWithTags(List.of("1"))).thenReturn(List.of(p));
        Page<PlanDto> result = planService.getPlansSorted("popular", 0, 5);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("페이징: unknown → fallback")
    void getPlansSorted_page_unknown() {
        Plan p = Plan.builder().id("1").build();
        when(planRepository.findPopularPlans()).thenReturn(List.of("1"));
        when(planRepository.findByIdsWithTags(List.of("1"))).thenReturn(List.of(p));
        Page<PlanDto> result = planService.getPlansSorted("xyz", 0, 5);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getPlanDetail_success() {
        Plan plan = Plan.builder().id("x").name("test").build();
        when(planRepository.findById("x")).thenReturn(Optional.of(plan));
        assertThat(planService.getPlanDetail("x").getName()).isEqualTo("test");
    }

    @Test
    void getPlanDetail_notFound() {
        when(planRepository.findById("no")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> planService.getPlanDetail("no"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void comparePlans_success() {
        Plan p = Plan.builder().id("1").build();
        when(planRepository.findAllById(List.of("1"))).thenReturn(List.of(p));
        assertThat(planService.comparePlans(List.of("1"))).hasSize(1);
    }

    @Test
    void comparePlans_partialMissing() {
        Plan p = Plan.builder().id("1").build();
        when(planRepository.findAllById(List.of("1", "2"))).thenReturn(List.of(p));
        assertThatThrownBy(() -> planService.comparePlans(List.of("1", "2")))
                .isInstanceOf(NotFoundException.class);
    }
}
