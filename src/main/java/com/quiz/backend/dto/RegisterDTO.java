package com.quiz.backend.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String nickname;
    private String secretToken;
}