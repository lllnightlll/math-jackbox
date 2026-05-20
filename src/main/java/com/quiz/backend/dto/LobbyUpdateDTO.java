package com.quiz.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor // Нужен для сериализации в JSON
public class LobbyUpdateDTO {
    private String type = "LOBBY_UPDATE";
    private List<String> players;


    public LobbyUpdateDTO(List<String> players) {
        this.players = players;
    }
}