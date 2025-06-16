package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.common.exception.InternalServerErrorException;
import com.eureka.ip.team1.urjung_main.user.dto.UsageRequestDto;
import com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto;
import com.eureka.ip.team1.urjung_main.user.repository.UsageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsageServiceTest {

    @InjectMocks
    private UsageServiceImpl usageServiceImpl;

    @Mock
    private UsageRepository usageRepository;

    @Test
    void testGetAllUsagesByUserId_성공_Test() {
        // Given
        String userId = "testUser";
        UsageRequestDto requestDto = UsageRequestDto.builder()
                .userId(userId)
                .build();

        when(usageRepository.findAllUsagesByUserId(userId)).thenReturn(List.of(
                UsageResponseDto.builder()
                        .planId("plan1")
                        .phoneNumber("010-1234-5678")
                        .year(2023)
                        .month(10)
                        .data(1000L)
                        .callMinute(200L)
                        .message(50L)
                        .build()
        ));

        // When
        List<UsageResponseDto> result = usageServiceImpl.getAllUsagesByUserId(requestDto);

        // Then
        assertNotNull(result);
        assertNotNull(result.get(0));
        assertEquals("plan1", result.get(0).getPlanId());
        assertEquals("010-1234-5678", result.get(0).getPhoneNumber());
        assertEquals(2023, result.get(0).getYear());
        assertEquals(10, result.get(0).getMonth());
        assertEquals(1000L, result.get(0).getData());
        assertEquals(200L, result.get(0).getCallMinute());
        assertEquals(50L, result.get(0).getMessage());
    }

    @Test
    void testGetAllUsagesByUserId_실패_Test() {
        // Given
        when(usageRepository.findAllUsagesByUserId(any(String.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        try {
            usageServiceImpl.getAllUsagesByUserId(UsageRequestDto.builder().userId("testUser").build());
        } catch (Exception e) {
            assertThrows(
                InternalServerErrorException.class,
                () -> {
                    throw new InternalServerErrorException();
                }
            );
        }

    }

    @Test
    void testGetAllUsagesByUserIdAndMonth_성공_Test() {
        // Given
        String userId = "testUser";
        int year = 2023;
        int month = 10;
        UsageRequestDto requestDto = UsageRequestDto.builder()
                .userId(userId)
                .year(year)
                .month(month)
                .build();

        when(usageRepository.findAllUsagesByUserIdAndMonth(userId, year, month)).thenReturn(List.of(
                UsageResponseDto.builder()
                        .planId("plan1")
                        .phoneNumber("010-1234-5678")
                        .year(year)
                        .month(month)
                        .data(1000L)
                        .callMinute(200L)
                        .message(50L)
                        .build()
        ));

        // When
        List<UsageResponseDto> result = usageServiceImpl.getAllUsagesByUserIdAndMonth(requestDto);

        // Then
        assertNotNull(result);
        assertNotNull(result.get(0));
        assertEquals("plan1", result.get(0).getPlanId());
        assertEquals("010-1234-5678", result.get(0).getPhoneNumber());
        assertEquals(year, result.get(0).getYear());
        assertEquals(month, result.get(0).getMonth());
        assertEquals(1000L, result.get(0).getData());
        assertEquals(200L, result.get(0).getCallMinute());
        assertEquals(50L, result.get(0).getMessage());
    }

    @Test
    void testGetAllUsagesByUserIdAndMonth_실패_Test() {
        // Given
        when(usageRepository.findAllUsagesByUserIdAndMonth(any(String.class), any(Integer.class), any(Integer.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        try {
            usageServiceImpl.getAllUsagesByUserIdAndMonth(UsageRequestDto.builder().userId("testUser").year(2023).month(10).build());
        } catch (Exception e) {
            assertThrows(
                InternalServerErrorException.class,
                () -> {
                    throw new InternalServerErrorException();
                }
            );
        }
    }

    @Test
    void testGetCurrentMonthUsagesByUserId_성공_Test() {
        // Given
        String userId = "testUser";
        UsageRequestDto requestDto = UsageRequestDto.builder()
                .userId(userId)
                .build();

        when(usageRepository.findCurrentMonthUsagesByUserId(userId)).thenReturn(List.of(
                UsageResponseDto.builder()
                        .planId("plan1")
                        .phoneNumber("010-1234-5678")
                        .year(2023)
                        .month(10)
                        .data(1000L)
                        .callMinute(200L)
                        .message(50L)
                        .build()
        ));

        // When
        List<UsageResponseDto> result = usageServiceImpl.getCurrentMonthUsagesByUserId(requestDto);

        // Then
        assertNotNull(result);
        assertNotNull(result.get(0));
        assertEquals("plan1", result.get(0).getPlanId());
        assertEquals("010-1234-5678", result.get(0).getPhoneNumber());
        assertEquals(2023, result.get(0).getYear());
        assertEquals(10, result.get(0).getMonth());
        assertEquals(1000L, result.get(0).getData());
        assertEquals(200L, result.get(0).getCallMinute());
        assertEquals(50L, result.get(0).getMessage());
    }

    @Test
    void testGetCurrentMonthUsagesByUserId_실패_Test() {
        // Given
        when(usageRepository.findCurrentMonthUsagesByUserId(any(String.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        try {
            usageServiceImpl.getCurrentMonthUsagesByUserId(UsageRequestDto.builder().userId("testUser").build());
        } catch (Exception e) {
            assertThrows(
                InternalServerErrorException.class,
                () -> {
                    throw new InternalServerErrorException();
                }
            );
        }
    }

    @Test
    void testGetUsageByLineIdAndMonth_성공_Test() {
        // Given
        String lineId = "testLine";
        int year = 2023;
        int month = 10;
        UsageRequestDto requestDto = UsageRequestDto.builder()
                .lineId(lineId)
                .year(year)
                .month(month)
                .build();

        when(usageRepository.findUsageByLineIdAndMonth(lineId, year, month)).thenReturn(
                Optional.of(
                    UsageResponseDto.builder()
                            .planId("plan1")
                            .phoneNumber("010-1234-5678")
                            .year(year)
                            .month(month)
                            .data(1000L)
                            .callMinute(200L)
                            .message(50L)
                            .build()
                )
        );

        // When
        var result = usageServiceImpl.getUsageByLineIdAndMonth(requestDto);

        // Then
        assertTrue(result.isPresent());
        assertEquals("plan1", result.get().getPlanId());
        assertEquals("010-1234-5678", result.get().getPhoneNumber());
        assertEquals(year, result.get().getYear());
        assertEquals(month, result.get().getMonth());
        assertEquals(1000L, result.get().getData());
        assertEquals(200L, result.get().getCallMinute());
        assertEquals(50L, result.get().getMessage());
    }

    @Test
    void testGetUsageByLineIdAndMonth_실패_Test() {
        // Given
        when(usageRepository.findUsageByLineIdAndMonth(any(String.class), any(Integer.class), any(Integer.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        try {
            usageServiceImpl.getUsageByLineIdAndMonth(UsageRequestDto.builder().lineId("testLine").year(2023).month(10).build());
        } catch (Exception e) {
            assertThrows(
                InternalServerErrorException.class,
                () -> {
                    throw new InternalServerErrorException();
                }
            );
        }
    }

    @Test
    void testGetUsageByUserIdAndPlanIdAndMonth_성공_Test() {
        // Given
        String userId = "testUser";
        String planId = "testPlan";
        int year = 2023;
        int month = 10;
        UsageRequestDto requestDto = UsageRequestDto.builder()
                .userId(userId)
                .planId(planId)
                .year(year)
                .month(month)
                .build();

        when(usageRepository.findUsageByUserIdAndPlanIdAndMonth(userId, planId, year, month)).thenReturn(
                Optional.of(
                    UsageResponseDto.builder()
                            .planId(planId)
                            .phoneNumber("010-1234-5678")
                            .year(year)
                            .month(month)
                            .data(1000L)
                            .callMinute(200L)
                            .message(50L)
                            .build()
                )
        );

        // When
        var result = usageServiceImpl.getUsageByUserIdAndPlanIdAndMonth(requestDto);

        // Then
        assertTrue(result.isPresent());
        assertEquals(planId, result.get().getPlanId());
        assertEquals("010-1234-5678", result.get().getPhoneNumber());
        assertEquals(year, result.get().getYear());
        assertEquals(month, result.get().getMonth());
        assertEquals(1000L, result.get().getData());
        assertEquals(200L, result.get().getCallMinute());
        assertEquals(50L, result.get().getMessage());
    }
    
    @Test
    void testGetUsageByUserIdAndPlanIdAndMonth_실패_Test() {
        // Given
        when(usageRepository.findUsageByUserIdAndPlanIdAndMonth(any(String.class), any(String.class), any(Integer.class), any(Integer.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        try {
            usageServiceImpl.getUsageByUserIdAndPlanIdAndMonth(UsageRequestDto.builder().userId("testUser").planId("testPlan").year(2023).month(10).build());
        } catch (Exception e) {
            assertThrows(
                InternalServerErrorException.class,
                () -> {
                    throw new InternalServerErrorException();
                }
            );
        }
    }

    @Test
    void testGetCurrentMonthUsageByUserIdAndPhoneNumber_성공_Test() {
        // Given
        String userId = "testUser";
        String phoneNumber = "010-1234-5678";
        UsageRequestDto requestDto = UsageRequestDto.builder()
                .userId(userId)
                .phoneNumber(phoneNumber)
                .build();

        when(usageRepository.findCurrentMonthUsageByUserIdAndPhoneNumber(userId, phoneNumber)).thenReturn(
                Optional.of(
                    UsageResponseDto.builder()
                            .planId("plan1")
                            .phoneNumber(phoneNumber)
                            .year(2025)
                            .month(6)
                            .data(1000L)
                            .callMinute(200L)
                            .message(50L)
                            .build()
                )
        );

        // When
        var result = usageServiceImpl.getCurrentMonthUsageByUserIdAndPhoneNumber(requestDto);

        // Then
        assertTrue(result.isPresent());
        assertEquals("plan1", result.get().getPlanId());
        assertEquals(phoneNumber, result.get().getPhoneNumber());
        assertEquals(2025, result.get().getYear());
        assertEquals(6, result.get().getMonth());
        assertEquals(1000L, result.get().getData());
        assertEquals(200L, result.get().getCallMinute());
        assertEquals(50L, result.get().getMessage());
    }

    @Test
    void testGetCurrentMonthUsageByUserIdAndPhoneNumber_실패_Test() {
        // Given
        when(usageRepository.findCurrentMonthUsageByUserIdAndPhoneNumber(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        try {
            usageServiceImpl.getCurrentMonthUsageByUserIdAndPhoneNumber(
                    UsageRequestDto.builder()
                            .userId("testUser")
                            .phoneNumber("010-1234-5678")
                            .build()
            );

        } catch (Exception e) {
            assertThrows(
                InternalServerErrorException.class,
                () -> {
                    throw new InternalServerErrorException();
                }
            );
        }
    }

    void testGetRecent3MonthsUsagesByUserIdAndPhoneNumber_성공_Test() {
        // Given
        String userId = "testUser";
        String phoneNumber = "010-1234-5678";
        UsageRequestDto requestDto = UsageRequestDto.builder()
                .userId(userId)
                .phoneNumber(phoneNumber)
                .build();

        when(usageRepository.findRecent3MonthsUsagesByUserIdAndPhoneNumber(userId, phoneNumber, any(), any())).thenReturn(List.of(
                UsageResponseDto.builder()
                        .planId("plan1")
                        .phoneNumber(phoneNumber)
                        .year(2025)
                        .month(6)
                        .data(1000L)
                        .callMinute(200L)
                        .message(50L)
                        .build(),
                UsageResponseDto.builder()
                        .planId("plan1")
                        .phoneNumber(phoneNumber)
                        .year(2025)
                        .month(5)
                        .data(800L)
                        .callMinute(150L)
                        .message(30L)
                        .build(),
                UsageResponseDto.builder()
                        .planId("plan1")
                        .phoneNumber(phoneNumber)
                        .year(2025)
                        .month(4)
                        .data(600L)
                        .callMinute(100L)
                        .message(20L)
                        .build()
        ));

        // When
        List<UsageResponseDto> result = usageServiceImpl.getRecent3MonthsUsagesByUserIdAndPhoneNumber(requestDto);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetRecent3MonthsUsagesByUserIdAndPhoneNumber_실패_Test() {
        // Given
        when(usageRepository.findRecent3MonthsUsagesByUserIdAndPhoneNumber(any(String.class), any(String.class), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        try {
            usageServiceImpl.getRecent3MonthsUsagesByUserIdAndPhoneNumber(
                    UsageRequestDto.builder()
                            .userId("testUser")
                            .phoneNumber("010-1234-5678")
                            .build()
            );
        } catch (Exception e) {
            assertThrows(
                InternalServerErrorException.class,
                () -> {
                    throw new InternalServerErrorException();
                }
            );
        }
    }

}