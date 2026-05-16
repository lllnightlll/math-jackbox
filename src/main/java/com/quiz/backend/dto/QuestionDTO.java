package com.quiz.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private String type = "NEW_QUESTION";
    private String text;
    private List<String> options;
    private int secondsLeft;
}