package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.common.exception.InternalServerErrorException;
import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
import com.eureka.ip.team1.urjung_main.membership.entity.Membership;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.dto.UserInfoDto;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserServiceImpl userService = new UserServiceImpl(userRepository);

    @Test
    @DisplayName("findById - 유저 정상 조회")
    void findById_success() {
        // given
        User user = new User();
        user.setUserId("user-200");
        user.setName("이정훈");
        user.setEmail("lee@example.com");
        user.setGender("M");
        user.setBirth(LocalDate.of(1995, 3, 15));

        when(userRepository.findById("user-200")).thenReturn(Optional.of(user));

        // when
        UserDto dto = userService.findById("user-200");

        // then
        assertThat(dto.getUserId()).isEqualTo("user-200");
        assertThat(dto.getName()).isEqualTo("이정훈");
        assertThat(dto.getEmail()).isEqualTo("lee@example.com");
        assertThat(dto.getGender()).isEqualTo("M");
        assertThat(dto.getBirth()).isEqualTo(LocalDate.of(1995, 3, 15));
    }

    @Test
    @DisplayName("findById - 유저 없음 -> InternalServerErrorException 반환")
    void findById_notFound() {
        when(userRepository.findById("not-exist")).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.findById("not-exist"))
                .isInstanceOf(InternalServerErrorException.class);
    }


    @Test
    @DisplayName("유저 정보 정상 반환")
    void getUserInfoDto_success() {
        // given
        Membership membership = new Membership();
        membership.setId(UUID.fromString("4b312ff0-8e7d-4d32-86ab-a4f5b9339910"));
        membership.setMembershipName("VIP");

        User user = new User();
        user.setUserId("user-123");
        user.setName("홍길동");
        user.setEmail("hong@example.com");
        user.setGender("MALE");
        user.setBirth(LocalDate.of(1990, 1, 1));
        user.setMembership(membership);

        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));

        // when
        UserInfoDto dto = userService.getUserInfoDto("user-123");

        // then
        assertThat(dto.getName()).isEqualTo("홍길동");
        assertThat(dto.getMembershipName()).isEqualTo("VIP");
        verify(userRepository, times(1)).findById("user-123");
    }

    @Test
    @DisplayName("유저 정보 없음 - 예외 발생")
    void getUserInfoDto_notFound() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserInfoDto("missing"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 유저가 없습니다.");
    }

    @Test
    @DisplayName("예외 발생 시 InternalServerError 처리")
    void getUserInfoDto_internalError() {
        when(userRepository.findById("error")).thenThrow(new RuntimeException("DB Error"));

        assertThatThrownBy(() -> userService.getUserInfoDto("error"))
                .isInstanceOf(InternalServerErrorException.class);
    }
}

