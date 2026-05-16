package com.quiz.backend.service;

import com.quiz.backend.dto.QuestionDTO;
import com.quiz.backend.model.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final SimpMessagingTemplate messagingTemplate;

    // Храним баллы игроков: Ключ - sessionId, Значение - баллы
    private final Map<String, Integer> playerScores = new ConcurrentHashMap<>();

    private final List<Question> questions = new ArrayList<>(List.of(
            new Question("1", "2 + 2 = ?", List.of("3", "4", "5"), 1),
            new Question("2", "Столица Франции?", List.of("Париж", "Лондон", "Берлин"), 0),
            new Question("3", "Java - это...", List.of("Кофе", "Язык", "Остров"), 1)
    ));

    private int currentQuestionIndex = 0;
    private Question currentQuestionObject; // Храним текущий вопрос целиком

    @Scheduled(fixedRate = 10000)
    public void nextQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            log.info("Игра окончена! Итоговые очки: {}", playerScores);
            messagingTemplate.convertAndSend("/topic/game", "ИГРА ОКОНЧЕНА. Очки: " + playerScores);
            currentQuestionIndex = 0;
            return;
        }

        // 1. Запоминаем текущий вопрос перед отправкой
        currentQuestionObject = questions.get(currentQuestionIndex);

        QuestionDTO dto = new QuestionDTO();
        dto.setText(currentQuestionObject.getText());
        dto.setOptions(currentQuestionObject.getOptions());
        dto.setSecondsLeft(10);

        log.info("--- НОВЫЙ РАУНД ---");
        log.info("Вопрос: {}", dto.getText());

        messagingTemplate.convertAndSend("/topic/game", dto);
        currentQuestionIndex++;
    }

    public void processAnswer(String sessionId, int answerIndex) {
        if (currentQuestionObject == null) return;

        // 2. Сверяем индекс ответа с правильным в текущем вопросе
        boolean isCorrect = (answerIndex == currentQuestionObject.getCorrectOptionIndex());

        if (isCorrect) {
            // Начисляем 10 баллов. getOrDefault защищает от ошибок, если игрока еще нет в карте
            int newScore = playerScores.getOrDefault(sessionId, 0) + 10;
            playerScores.put(sessionId, newScore);
            log.info("Игрок {} ответил ПРАВИЛЬНО! Очков: {}", sessionId, newScore);
        } else {
            log.info("Игрок {} ошибся.", sessionId);
        }

        // 3. Отправим игроку ЛИЧНОЕ сообщение (не всем), правильно он ответил или нет
        // В STOMP для этого есть механизм, но для MVP можно пока просто слать в общий лог или оставить так.
    }
}