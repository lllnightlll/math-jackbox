package com.quiz.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResultDTO {
    private String type = "GameResult";
    private List<PlayerScore> scores;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerScore {
        private String nickname;
        private int score;
    }
}
