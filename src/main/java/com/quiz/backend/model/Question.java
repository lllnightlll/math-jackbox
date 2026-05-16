package com.quiz.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

//ЗАГЛУШКА ВМЕСТО БД
@Data
@AllArgsConstructor
public class Question {
    private String id;
    private String text;
    private List<String> options;
    private int correctOptionIndex;
}