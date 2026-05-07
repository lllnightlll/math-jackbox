package com.quiz.backend.controller;

import com.quiz.backend.dto.AnswerDTO;
import com.quiz.backend.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // Фронтенд присылает ответ на /app/answer
    @MessageMapping("/answer")
    public void handleAnswer(AnswerDTO answer, @Header("simpSessionId") String sessionId) {
        // Мы используем @Header("simpSessionId"), чтобы знать, КТО ответил
        quizService.processAnswer(sessionId, answer.getChosenOptionIndex());
    }
}