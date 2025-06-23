package com.eureka.ip.team1.urjung_main.forbiddenword.repository;

import com.eureka.ip.team1.urjung_main.chatbot.entity.ForbiddenWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForbiddenWordRepository extends JpaRepository<ForbiddenWord, UUID> {
   Optional<ForbiddenWord>  findByWord(String word);  // 테스트용 레포지토리 입니다
}
