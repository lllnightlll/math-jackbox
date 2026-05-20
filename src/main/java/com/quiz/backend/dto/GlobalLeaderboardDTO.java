package com.quiz.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalLeaderboardDTO {
    private String type = "GLOBAL_LEADERBOARD";
    private List<PlayerRow> leaderboard;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerRow {
        private String nickname;
        private int globalScore;
    }
}