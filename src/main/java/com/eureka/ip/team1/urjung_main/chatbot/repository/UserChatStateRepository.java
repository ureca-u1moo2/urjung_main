package com.eureka.ip.team1.urjung_main.chatbot.repository;

import com.eureka.ip.team1.urjung_main.chatbot.entity.UserChatState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChatStateRepository extends CrudRepository<UserChatState, String> {
}