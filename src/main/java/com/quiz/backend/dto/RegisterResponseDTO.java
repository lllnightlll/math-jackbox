package com.quiz.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDTO {
    private String type = "REGISTER_SUCCESS";
    private String secretToken;
    private String nickname;
}