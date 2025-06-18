package com.eureka.ip.team1.urjung_main.user.repository;

import com.eureka.ip.team1.urjung_main.membership.entity.Membership;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager; // Membership 먼저 영속화 필요

    @Test
    @DisplayName("이메일로 유저 조회 성공 - Membership 포함")
    void findByEmailSuccess() {
        // given
        Membership membership = new Membership();
        membership.setMembershipName("일반");
        membership.setRequireAmount(10000);
        membership.setGiftDiscount(0.1);

        entityManager.persist(membership);
        entityManager.flush(); //

        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .email("tester@abc.com")
                .password("1234")
                .name("김테스트")
                .gender("M")
                .birth(LocalDate.of(1990, 1, 1))
                .membership(membership)
                .build();

        userRepository.save(user);
        entityManager.flush(); //

        // when
        Optional<User> result = userRepository.findByEmail("tester@abc.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getMembership().getMembershipName()).isEqualTo("일반");
    }
}