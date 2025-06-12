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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPlanServiceTest {

    @InjectMocks
    private UserPlanServiceImpl userPlanService;

    @Mock
    private UserPlanRepository userPlanRepository;

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
                        .discountedPrice(10000)
                        .build();

        when(userPlanRepository.findAllPlansByUserId(userId))
                .thenReturn(List.of(userPlanResponseDto));

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
}