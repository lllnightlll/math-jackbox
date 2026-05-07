package com.quiz.backend.model;

import lombok.Data;

@Data
public class Player {
    private String sessionId; // ID сессии в вебсокете
    private String nickname;
    private int score;
}