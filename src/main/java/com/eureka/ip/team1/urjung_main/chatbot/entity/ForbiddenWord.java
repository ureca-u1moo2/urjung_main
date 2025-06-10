package com.eureka.ip.team1.urjung_main.chatbot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "forbidden_word")
@Getter
@Setter
@NoArgsConstructor
public class ForbiddenWord {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String word;

    public ForbiddenWord(String word) {
        this.word = word;
    }
}
