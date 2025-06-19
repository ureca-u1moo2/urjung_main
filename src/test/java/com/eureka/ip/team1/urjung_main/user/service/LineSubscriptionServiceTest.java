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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LineSubscriptionServiceTest {

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

    // 사용자의 회선 전체 조회 테스트
    @Test
    void getAllLinesByUserId_shouldReturnDtoList() {

        Plan plan1 = new Plan();
        plan1.setId("plan001");
        plan1.setName("요금제 A");

        Plan plan2 = new Plan();
        plan2.setId("plan002");
        plan2.setName("요금제 B");

        Line line1 = Line.builder()
                .id("line1")
                .userId("user123")
                .phoneNumber("010-1000-1000")
                .planId("plan001")
                .plan(plan1)
                .status(Line.LineStatus.active)
                .startDate(LocalDateTime.now())
                .discountedPrice(20000)
                .build();

        Line line2 = Line.builder()
                .id("line2")
                .userId("user123")
                .phoneNumber("010-2000-2000")
                .planId("plan002")
                .plan(plan2)
                .status(Line.LineStatus.canceled)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .discountedPrice(25000)
                .build();

        when(lineRepository.findAllByUserId("user123")).thenReturn(List.of(line1, line2));

        var result = service.getAllLinesByUserId("user123");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("line1");
        assertThat(result.get(1).getPhoneNumber()).isEqualTo("010-2000-2000");
    }

    // membership 의 discount 가 null 인 상황
    @Test
    void getDiscountedPrice_shouldReturnOriginalPriceWhenDiscountIsNull() {
        // given
        Plan plan = new Plan();
        plan.setId("plan123");
        plan.setPrice(10000);

        Membership membership = new Membership(); // discountRate not set (null)

        User user = new User();
        user.setUserId("user123");
        user.setMembership(membership);

        when(planRepository.findById("plan123")).thenReturn(Optional.of(plan));
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        // when
        int discountedPrice = service.getDiscountedPrice("user123", "plan123");

        // then
        assertThat(discountedPrice).isEqualTo(10000); // no discount applied
    }

    // 해지했던 번호 다시 그 번호로 가입하려고 할 때
    @Test
    void subscribe_shouldReviveCanceledLineIfExists() {
        // given
        Line canceledLine = Line.builder()
                .id("line123")
                .userId("user123")
                .planId("old-plan")
                .phoneNumber("010-1234-5678")
                .status(Line.LineStatus.canceled)
                .discountedPrice(8000)
                .startDate(LocalDateTime.now().minusMonths(3))
                .endDate(LocalDateTime.now().minusDays(1))
                .build();

        when(planRepository.findById("plan123")).thenReturn(Optional.of(plan));
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(lineRepository.findByPhoneNumber("010-1234-5678")).thenReturn(Optional.of(canceledLine));

        // when
        service.subscribe(dto, "user123");

        // then
        assertThat(canceledLine.getStatus()).isEqualTo(Line.LineStatus.active);
        assertThat(canceledLine.getPlanId()).isEqualTo("plan123");
        assertThat(canceledLine.getEndDate()).isNull();
        assertThat(canceledLine.getDiscountedPrice()).isEqualTo(9000); // 10% 할인

        verify(lineRepository).save(canceledLine);
    }

    @Test
    void subscribe_shouldCreateNewLineIfExistingLineIsActive() {
        // given: 이미 등록된 전화번호지만 상태가 active인 회선 존재
        Line activeLine = Line.builder()
                .id("line-active")
                .userId("user123")
                .planId("other-plan")
                .phoneNumber("010-1234-5678")
                .status(Line.LineStatus.active)
                .discountedPrice(8000)
                .startDate(LocalDateTime.now().minusMonths(1))
                .build();

        when(planRepository.findById("plan123")).thenReturn(Optional.of(plan));
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(lineRepository.findByPhoneNumber("010-1234-5678")).thenReturn(Optional.of(activeLine));

        // when
        service.subscribe(dto, "user123");

        // then
        // 기존 회선은 재활성화 대상이 아님 -> 새 회선 생성
        verify(lineRepository, times(1)).save(argThat(line ->
                        line.getPhoneNumber().equals("010-1234-5678") &&
                        line.getPlanId().equals("plan123") &&
                        line.getStatus() == Line.LineStatus.active
        ));
    }




}
