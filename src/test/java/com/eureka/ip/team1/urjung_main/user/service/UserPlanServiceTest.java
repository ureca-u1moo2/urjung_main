package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.user.dto.UserPlanResponseDto;
import com.eureka.ip.team1.urjung_main.user.repository.UserPlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPlanServiceTest {

    @InjectMocks
    private UserPlanServiceImpl userPlanService;

    @Mock
    private UserPlanRepository userPlanRepository;

    @Mock
    private LineSubscriptionService lineSubscriptionService;

    @Test
    void testFindAllPlansByUserId_성공_Test() {
        // Given
        String userId = "testUserId";

        UserPlanResponseDto userPlanResponseDto = UserPlanResponseDto.builder()
                        .planId("plan-001")
                        .planName("기본 요금제")
                        .phoneNumber("123-1234-1234")
                        .description("기본 요금제 설명")
                        .startDate(LocalDateTime.of(2023, 10, 1, 0, 0))
                        .discountedPrice(0)
                        .build();

        when(userPlanRepository.findAllPlansByUserId(userId))
                .thenReturn(List.of(userPlanResponseDto));

        // 할인된 가격
        when(lineSubscriptionService.getDiscountedPrice(userId, "plan-001"))
                .thenReturn(10000);


        // When
        List<UserPlanResponseDto> allPlansByUserId = userPlanService.findAllPlansByUserId(userId);

        // Then
        assertNotNull(allPlansByUserId);
        assertEquals(1, allPlansByUserId.size());
        assertEquals("plan-001", allPlansByUserId.get(0).getPlanId());
        assertEquals("기본 요금제", allPlansByUserId.get(0).getPlanName());
        assertEquals("123-1234-1234", allPlansByUserId.get(0).getPhoneNumber());
        assertEquals("기본 요금제 설명", allPlansByUserId.get(0).getDescription());
        assertEquals(LocalDateTime.of(2023, 10, 1, 0, 0), allPlansByUserId.get(0).getStartDate());
        assertEquals(10000, allPlansByUserId.get(0).getDiscountedPrice());
    }

    // 할인 가격 계산
    void testFindAllPlansByUserId_성공_실시간할인적용() {
        // given
        String userId = "testUserId";

        UserPlanResponseDto dto = UserPlanResponseDto.builder()
                .planId("plan-001")
                .planName("기본 요금제")
                .phoneNumber("010-1234-5678")
                .description("기본 요금제 설명")
                .startDate(LocalDateTime.of(2023, 10, 1, 0, 0))
                .discountedPrice(0) // 초기값은 0으로 가정
                .build();

        when(userPlanRepository.findAllPlansByUserId(userId)).thenReturn(List.of(dto));
        when(lineSubscriptionService.getDiscountedPrice(userId, "plan-001")).thenReturn(8900);

        // when
        List<UserPlanResponseDto> result = userPlanService.findAllPlansByUserId(userId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        UserPlanResponseDto resultDto = result.get(0);
        assertEquals("plan-001", resultDto.getPlanId());
        assertEquals("기본 요금제", resultDto.getPlanName());
        assertEquals("010-1234-5678", resultDto.getPhoneNumber());
        assertEquals("기본 요금제 설명", resultDto.getDescription());
        assertEquals(LocalDateTime.of(2023, 10, 1, 0, 0), resultDto.getStartDate());
        assertEquals(8900, resultDto.getDiscountedPrice()); // 실시간 할인 가격이 덮어씌워졌는지 확인

        // verify
        verify(userPlanRepository, times(1)).findAllPlansByUserId(userId);
        verify(lineSubscriptionService, times(1)).getDiscountedPrice(userId, "plan-001");
    }

    @Test
    void testFindAllPlansByUserId_할인계산_실패해도_진행됨() {
        // given
        String userId = "testUserId";

        UserPlanResponseDto dto = UserPlanResponseDto.builder()
                .planId("plan-002")
                .planName("프리미엄 요금제")
                .phoneNumber("010-9876-5432")
                .description("프리미엄 요금제 설명")
                .startDate(LocalDateTime.of(2024, 1, 1, 0, 0))
                .discountedPrice(0)
                .build();

        when(userPlanRepository.findAllPlansByUserId(userId)).thenReturn(List.of(dto));
        when(lineSubscriptionService.getDiscountedPrice(userId, "plan-002"))
                .thenThrow(new RuntimeException("Discount service error"));

        // when
        List<UserPlanResponseDto> result = userPlanService.findAllPlansByUserId(userId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getDiscountedPrice()); // 할인 실패했으므로 기존값 유지

        // verify
        verify(userPlanRepository).findAllPlansByUserId(userId);
        verify(lineSubscriptionService).getDiscountedPrice(userId, "plan-002");
    }

    @Test
    void testFindAllPlansByUserId_Repository_예외_처리() {
        // given
        String userId = "errorUser";
        when(userPlanRepository.findAllPlansByUserId(userId)).thenThrow(new RuntimeException("DB error"));

        // then
        assertThrows(RuntimeException.class, () -> {
            userPlanService.findAllPlansByUserId(userId);
        });
    }
}