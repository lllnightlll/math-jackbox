package com.quiz.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "players")
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String secretToken;


    @Column(unique = true)
    private String sessionId;


    @Column(nullable = false)
    private String nickname;

    private int score;

}