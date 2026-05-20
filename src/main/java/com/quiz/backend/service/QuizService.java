package com.quiz.backend.service;

import com.quiz.backend.dto.GameResultDTO;
import com.quiz.backend.dto.QuestionDTO;
import com.quiz.backend.model.Player;
import com.quiz.backend.model.Question;
import com.quiz.backend.repository.PlayerRepository;
import com.quiz.backend.repository.QuestionRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final SimpMessagingTemplate messagingTemplate;
    private final PlayerRepository playerRepository;
    private final QuestionRepository questionRepository;

    // Храним баллы игроков: Ключ - sessionId, Значение - баллы
    private final Map<String, Integer> playerScores = new ConcurrentHashMap<>();
    // Храним уникальные sessionId ответивших игроков
    private final Set<String> answeredPlayers = ConcurrentHashMap.newKeySet();

    private int currentQuestionIndex = 0;
    private Question currentQuestionObject;

    @Scheduled(fixedRate = 10000)
    @Transactional(readOnly = true)
    public void nextQuestion() {
        List<Question> questions = questionRepository.findAllWithOptions();

        if (questions.isEmpty()) {
            log.warn("В базе данных нет вопросов!");
            return;
        }

        // Выводим отсортированный по очкам список игроков по окончанию игры
        if (currentQuestionIndex >= questions.size()) {
            log.info("Игра окончена! Записываем результаты в БД...");

            // 1. Обновляем глобальный счет каждого игрока в БД
            playerScores.forEach((sessionId, matchScore) -> {
                playerRepository.findBySessionId(sessionId).ifPresent(player -> {
                    // Прибавляем очки за текущий матч к глобальным очкам в БД
                    player.setScore(player.getScore() + matchScore);
                    playerRepository.save(player); // Сохраняем в БД
                });
            });

            // 2. Формируем результаты текущего матча (для отправки игрокам)
            List<GameResultDTO.PlayerScore> matchResult =
                    playerScores.entrySet()
                            .stream()
                    .map(entry -> {
                        String nickname = playerRepository.findBySessionId(entry.getKey())
                                .map(Player::getNickname)
                                .orElse("UNKNOWN");
                        return new GameResultDTO.PlayerScore(nickname, entry.getValue());
                    })
                    .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                    .toList();

            // Отправляем результаты матча
            messagingTemplate.convertAndSend("/topic/game", new GameResultDTO("GAME_RESULT", matchResult));

            // 3. Достаем ТОП-10 игроков за всё время из БД и отправляем Глобальный Рейтинг
            List<GlobalLeaderboardDTO.PlayerRow> globalTop = playerRepository.findTop10ByOrderByScoreDesc()
                    .stream()
                    .map(p -> new GlobalLeaderboardDTO.PlayerRow(p.getNickname(), p.getScore()))
                    .toList();

            // Отправляем глобальный топ
            messagingTemplate.convertAndSend("/topic/game", new GlobalLeaderboardDTO("GLOBAL_LEADERBOARD", globalTop));

            // Сбрасываем игру
            currentQuestionIndex = 0;
            playerScores.clear();
            return;
        }

        answeredPlayers.clear();
        log.info("Новый раунд: сброс ответивших игроков");

        currentQuestionObject = questions.get(currentQuestionIndex);

        QuestionDTO dto = new QuestionDTO();
        dto.setText(currentQuestionObject.getText());
        dto.setOptions(new ArrayList<>(currentQuestionObject.getOptions()));
        dto.setSecondsLeft(10);

        log.info("--- НОВЫЙ РАУНД ---");
        log.info("Вопрос: {}", dto.getText());

        messagingTemplate.convertAndSend("/topic/game", dto);
        currentQuestionIndex++;
    }

    public void processAnswer(String sessionId, int answerIndex) {
        if (currentQuestionObject != null && (answerIndex < 0 || answerIndex >= currentQuestionObject.getOptions().size())) {
            log.warn("Игрок {} отправил невалидный индекс: {}", sessionId, answerIndex);
            return;
        }

        if (!answeredPlayers.add(sessionId)) {
            log.info("Игрок {} уже ответил!", sessionId);
            return;
        }

        if (currentQuestionObject == null) {
            answeredPlayers.remove(sessionId);
            return;
        }

        Optional<Player> playerOpt = playerRepository.findBySessionId(sessionId);
        Player player = playerOpt.orElseGet(() -> {
            Player newPlayer = new Player();
            newPlayer.setSessionId(sessionId);
            newPlayer.setNickname("Player_" + sessionId.substring(0,8));
            newPlayer.setScore(0);
            return newPlayer;
                });

        // 2. Сверяем индекс ответа с правильным в текущем вопросе
        boolean isCorrect = (answerIndex == currentQuestionObject.getCorrectOptionIndex());

        if (isCorrect) {
            // Начисляем 10 баллов. getOrDefault защищает от ошибок, если игрока еще нет в карте
            int newScore = playerScores.getOrDefault(sessionId, 0) + 10;
            playerScores.put(sessionId, newScore);
            player.setScore(newScore);
            playerRepository.save(player);
            log.info("Игрок {} ответил ПРАВИЛЬНО! Очков: {}", sessionId, newScore);
        } else {
            log.info("Игрок {} ошибся.", sessionId);
        }

        // 3. Отправим игроку ЛИЧНОЕ сообщение (не всем), правильно он ответил или нет
        // В STOMP для этого есть механизм, но для MVP можно пока просто слать в общий лог или оставить так.
    }
}