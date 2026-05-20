package com.quiz.backend.service;

import com.quiz.backend.dto.GameResultDTO;
import com.quiz.backend.dto.GlobalLeaderboardDTO;
import com.quiz.backend.dto.LobbyUpdateDTO;
import com.quiz.backend.dto.QuestionDTO;
import com.quiz.backend.model.Player;
import com.quiz.backend.model.Question;
import com.quiz.backend.repository.PlayerRepository;
import com.quiz.backend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // --- БЛОК ОКОНЧАНИЯ ИГРЫ ---
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
            try {
                Thread.sleep(5000); // пауза в 2 секунды
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
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

        // Проверяем, зарегистрирован ли игрок
        Optional<Player> playerOpt = playerRepository.findBySessionId(sessionId);
        if (playerOpt.isEmpty()) {
            log.warn("Попытка ответить без регистрации: {}", sessionId);
            answeredPlayers.remove(sessionId);
            return;
        }

        // Проверяем ответ
        boolean isCorrect = (answerIndex == currentQuestionObject.getCorrectOptionIndex());

        if (isCorrect) {
            // ТЕПЕРЬ МЫ МЕНЯЕМ ОЧКИ ТОЛЬКО В ОПЕРАТИВКЕ (playerScores)
            // Базу данных во время раунда мы вообще не трогаем!
            int newScore = playerScores.getOrDefault(sessionId, 0) + 10;
            playerScores.put(sessionId, newScore);
            log.info("Игрок {} ответил ПРАВИЛЬНО! Очков в матче: {}", sessionId, newScore);
        } else {
            log.info("Игрок {} ошибся.", sessionId);
        }
    }

    @Transactional
    public void registerPlayer(String sessionId, String nickname) {
        // Ищем игрока в БД по НИКНЕЙМУ, а не по сессии!
        Player player = playerRepository.findByNickname(nickname) // Нужен метод в репозитории
                .orElse(new Player());

        // Обновляем сессию на актуальную (новую)
        player.setSessionId(sessionId);

        // Если это новый игрок, заполняем поля
        if (player.getId() == null) {
            player.setNickname(nickname);
            player.setScore(0);
        }

        playerRepository.save(player);

        // Инициализируем/обновляем его очки в мапе текущего матча
        playerScores.put(sessionId, 0);

        log.info("Игрок '{}' вошел в игру. Глобальный счет в БД: {}. Новая сессия: {}",
                nickname, player.getScore(), sessionId);

        broadcastLobbyUpdate();
    }

    private void broadcastLobbyUpdate() {
        // Достаем из БД имена всех активных игроков (у которых score в памяти инициализирован)
        List<String> activeNicknames = playerScores.keySet().stream()
                .map(sessionId -> playerRepository.findBySessionId(sessionId)
                        .map(Player::getNickname)
                        .orElse("Anonymous"))
                .toList();

        messagingTemplate.convertAndSend("/topic/game", new LobbyUpdateDTO(activeNicknames));
    }
}