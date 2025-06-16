package com.eureka.ip.team1.urjung_main.chatbot.repository;

import com.eureka.ip.team1.urjung_main.chatbot.entity.ChatContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatContextRedisRepository extends CrudRepository<ChatContext, String> {
    // 기본적인 save, findById, deleteById 제공
}
