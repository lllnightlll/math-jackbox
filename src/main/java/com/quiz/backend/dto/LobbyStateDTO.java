package com.quiz.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LobbyStateDTO {
    private String type = "LOBBY_STATE";
    private List<String> players;
    private int readyCount;
    private int questionCount;
    private int timeLeft;
}