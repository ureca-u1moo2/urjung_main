package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.common.exception.ForbiddenException;
import com.eureka.ip.team1.urjung_main.common.exception.InvalidInputException;
import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
import com.eureka.ip.team1.urjung_main.membership.entity.Membership;
import com.eureka.ip.team1.urjung_main.plan.entity.Plan;
import com.eureka.ip.team1.urjung_main.plan.repository.PlanRepository;
import com.eureka.ip.team1.urjung_main.user.dto.LineSubscriptionDto;
import com.eureka.ip.team1.urjung_main.user.entity.Line;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.repository.LineRepository;
import com.eureka.ip.team1.urjung_main.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LineSubscriptionServiceImplTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LineSubscriptionServiceImpl service;

    private LineSubscriptionDto dto;
    private Plan plan;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dto = new LineSubscriptionDto();
        dto.setPlanId("plan123");
        dto.setPhoneNumber("010-1234-5678");

        plan = new Plan();
        plan.setId("plan123");
        plan.setPrice(10000);

        Membership membership = new Membership();
        membership.setGiftDiscount(0.1);

        user = new User();
        user.setUserId("user123");
        user.setName("테스트");
        user.setEmail("test@example.com");
        user.setPassword("pwd1234");
        user.setGender("male");
        user.setBirth(LocalDate.of(2000, 1, 1));
        user.setMembership(membership);
    }

    @Test
    void subscribe_shouldSaveLine() {
        when(planRepository.findById("plan123")).thenReturn(Optional.of(plan));
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        service.subscribe(dto, "user123");

        verify(lineRepository, times(1)).save(any(Line.class));
    }

    @Test
    void subscribe_shouldThrowNotFoundExceptionWhenPlanNotFound() {
        when(planRepository.findById("plan123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.subscribe(dto, "user123"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void cancelLine_shouldSetCanceledStatus() {
        Line line = Line.builder().id("line1").userId("user123").status(Line.LineStatus.active).build();
        when(lineRepository.findById("line1")).thenReturn(Optional.of(line));

        service.cancelLine("user123", "line1");

        assertThat(line.getStatus()).isEqualTo(Line.LineStatus.canceled);
        verify(lineRepository).save(line);
    }

    @Test
    void cancelLine_shouldThrowNotFoundException() {
        when(lineRepository.findById("lineX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelLine("user123", "lineX"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void cancelLine_shouldThrowForbiddenException() {
        Line line = Line.builder().id("line1").userId("anotherUser").status(Line.LineStatus.active).build();
        when(lineRepository.findById("line1")).thenReturn(Optional.of(line));

        assertThatThrownBy(() -> service.cancelLine("user123", "line1"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void cancelLine_shouldThrowInvalidInputExceptionWhenAlreadyCanceled() {
        Line line = Line.builder().id("line1").userId("user123").status(Line.LineStatus.canceled).build();
        when(lineRepository.findById("line1")).thenReturn(Optional.of(line));

        assertThatThrownBy(() -> service.cancelLine("user123", "line1"))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void getDiscountedPrice_shouldReturnDiscountedValue() {
        when(planRepository.findById("plan123")).thenReturn(Optional.of(plan));
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        int price = service.getDiscountedPrice("user123", "plan123");

        assertThat(price).isEqualTo(9000);
    }

    @Test
    void getDiscountedPrice_shouldThrowIfPlanNotFound() {
        when(planRepository.findById("plan123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDiscountedPrice("user123", "plan123"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getDiscountedPrice_shouldThrowIfUserNotFound() {
        when(planRepository.findById("plan123")).thenReturn(Optional.of(plan));
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDiscountedPrice("user123", "plan123"))
                .isInstanceOf(NotFoundException.class);
    }
}
