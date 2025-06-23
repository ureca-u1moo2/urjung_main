package com.eureka.ip.team1.urjung_main.chatbot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
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
    @Column(name = "word_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID wordId;

//    @ManyToOne(fetch = FetchType.LAZY)    // 아직 관리자 부분 정리가 안끝나서 추후 주석풀고 진행하겠습니다.
//    @JoinColumn(name = "admin_id", nullable = false)
//    private Admin admin;

    @Column(name = "word", nullable = false, unique = true)
    private String word;

    @Column(name = "word_update", nullable = false) // refactor
    private LocalDateTime wordUpdate;

    @Column(name = "word_desc", columnDefinition = "TEXT")  // refactor
    private String wordDesc;

    @Column(name = "class", nullable = false) // refactor
    private String wordClass;

}
