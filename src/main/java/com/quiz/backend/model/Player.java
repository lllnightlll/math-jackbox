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

    private String sessionId; // ID сессии в вебсокете
    private String nickname;
    private int score;
}