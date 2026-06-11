package com.quiz.backend.service;

import com.quiz.backend.dto.*;
import com.quiz.backend.model.Player;
import com.quiz.backend.model.Question;
import com.quiz.backend.repository.PlayerRepository;
import com.quiz.backend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final SimpMessagingTemplate messagingTemplate;
    private final PlayerRepository playerRepository;
    private final QuestionRepository questionRepository;

    private final Map<String, Integer> playerScores = new ConcurrentHashMap<>();
    private final Map<String, String> sessionNicknames = new ConcurrentHashMap<>();
    private final Set<String> registeredSessions = ConcurrentHashMap.newKeySet();
    private final Set<String> answeredPlayers = ConcurrentHashMap.newKeySet();
    private final Set<String> readyPlayers = ConcurrentHashMap.newKeySet();

    private boolean isPlaying = false;
    private int lobbyTimer = 15;
    private int questionTimer = 0;
    private int targetQuestionCount = 5;

    private List<Question> currentMatchQuestions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private Question currentQuestionObject;

    @Transactional
    public void registerPlayer(String sessionId, RegisterDTO dto) {
        if (isPlaying) {
            sendError(sessionId, "Игра уже идет. Подождите окончания матча.");
            return;
        }

        String requestedNickname = dto.getNickname() != null ? dto.getNickname().trim() : "";

        if (requestedNickname.isEmpty()) {
            sendError(sessionId, "Никнейм не может быть пустым.");
            return;
        }

        Player player = null;


        if (dto.getSecretToken() != null && !dto.getSecretToken().isEmpty()) {
            Optional<Player> existingPlayer = playerRepository.findBySecretToken(dto.getSecretToken());
            if (existingPlayer.isPresent()) {
                player = existingPlayer.get();
            }
        }

        if (player == null) {
            if (playerRepository.findByNickname(requestedNickname).isPresent()) {
                sendError(sessionId, "Этот никнейм уже занят другим игроком. Выбери другой.");
                return;
            }

            if (sessionNicknames.containsValue(requestedNickname)) {
                sendError(sessionId, "Этот никнейм прямо сейчас используется в игре. Выбери другой.");
                return;
            }

            player = new Player();
            player.setNickname(requestedNickname);
            player.setScore(0);
            player.setSecretToken(UUID.randomUUID().toString());

            log.info("Создан новый игрок '{}'.", requestedNickname);
        }

        player.setSessionId(sessionId);
        playerRepository.save(player);

        playerScores.put(sessionId, 0);
        sessionNicknames.put(sessionId, player.getNickname());
        registeredSessions.add(sessionId);

        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/reply",
                new RegisterResponseDTO("REGISTER_SUCCESS", player.getSecretToken(), player.getNickname()),
                createHeaders(sessionId)
        );

        broadcastLobbyState();
    }

    public void joinAsGuest(String sessionId, String nickname) {
        if (isPlaying) {
            sendError(sessionId, "Игра уже идет. Подождите окончания матча.");
            return;
        }

        String guestName = (nickname == null || nickname.trim().isEmpty())
                ? "Guest_" + sessionId.substring(0, 4)
                : nickname.trim();

        if (playerRepository.findByNickname(guestName).isPresent() || sessionNicknames.containsValue(guestName)) {
            sendError(sessionId, "Никнейм '" + guestName + "' занят. Выбери другой или зайди без имени (выдастся случайное).");
            return;
        }

        playerScores.put(sessionId, 0);
        sessionNicknames.put(sessionId, guestName);

        log.info("Вошел гость: {}", guestName);

        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/reply",
                new RegisterResponseDTO("GUEST_SUCCESS", null, guestName),
                createHeaders(sessionId)
        );

        broadcastLobbyState();
    }

    // Вспомогательный метод для отправки ошибок конкретному пользователю
    private void sendError(String sessionId, String message) {
        log.warn("Ошибка входа (Сессия {}): {}", sessionId, message);
        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/reply",
                new ErrorDTO("ERROR", message),
                createHeaders(sessionId)
        );
    }

    public void setPlayerReady(String sessionId) {
        if (!isPlaying && sessionNicknames.containsKey(sessionId)) {
            readyPlayers.add(sessionId);
            broadcastLobbyState();
        }
    }

    public void changeQuestionCount(int count) {
        if (!isPlaying) {
            targetQuestionCount = count;
            broadcastLobbyState();
        }
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    // Игровой цикл - работает каждую 1 секунду
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void gameTick() {
        if (!isPlaying) {
            handleLobbyTick();
        } else {
            handlePlayingTick();
        }
    }

    private void handleLobbyTick() {
        if (sessionNicknames.isEmpty()) {
            lobbyTimer = 15;
            readyPlayers.clear();
            return;
        }

        boolean allReady = !readyPlayers.isEmpty() && readyPlayers.size() == sessionNicknames.size();

        if (lobbyTimer <= 0 || allReady) {
            startGame();
        } else {
            lobbyTimer--;
            broadcastLobbyState();
        }
    }


    protected void startGame() {
        List<Question> allQuestions = questionRepository.findAllWithOptions();
        if (allQuestions.isEmpty()) return;

        Collections.shuffle(allQuestions);
        int limit = Math.min(targetQuestionCount, allQuestions.size());
        currentMatchQuestions = new ArrayList<>(allQuestions.subList(0, limit));

        isPlaying = true;
        currentQuestionIndex = 0;
        questionTimer = 0; // Форсируем отправку первого вопроса
    }


    protected void handlePlayingTick() {
        boolean allAnswered = !answeredPlayers.isEmpty() && answeredPlayers.size() == sessionNicknames.size();

        if (questionTimer <= 0 || allAnswered) {
            if (currentQuestionIndex >= currentMatchQuestions.size()) {
                finishGame();
            } else {
                sendNextQuestion();
            }
        } else {
            questionTimer--;
        }
    }

    private void sendNextQuestion() {
        answeredPlayers.clear();
        currentQuestionObject = currentMatchQuestions.get(currentQuestionIndex);
        questionTimer = 10;

        QuestionDTO dto = new QuestionDTO();
        dto.setText(currentQuestionObject.getText());
        dto.setOptions(new ArrayList<>(currentQuestionObject.getOptions()));
        dto.setSecondsLeft(questionTimer);

        messagingTemplate.convertAndSend("/topic/game", dto);
        currentQuestionIndex++;
    }

    private void finishGame() {
        playerScores.forEach((sessionId, matchScore) -> {
            if (registeredSessions.contains(sessionId)) {
                playerRepository.findBySessionId(sessionId).ifPresent(player -> {
                    player.setScore(player.getScore() + matchScore);
                    playerRepository.save(player);
                });
            }
        });

        List<GameResultDTO.PlayerScore> matchResult = playerScores.entrySet().stream()
                .map(entry -> new GameResultDTO.PlayerScore(
                        sessionNicknames.getOrDefault(entry.getKey(), "Guest"), entry.getValue()))
                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                .toList();

        messagingTemplate.convertAndSend("/topic/game", new GameResultDTO("GAME_RESULT", matchResult));

        List<GlobalLeaderboardDTO.PlayerRow> globalTop = playerRepository.findTop10ByOrderByScoreDesc().stream()
                .map(p -> new GlobalLeaderboardDTO.PlayerRow(p.getNickname(), p.getScore()))
                .toList();

        messagingTemplate.convertAndSend("/topic/game", new GlobalLeaderboardDTO("GLOBAL_LEADERBOARD", globalTop));

        resetLobby();
    }

    private void resetLobby() {
        isPlaying = false;
        lobbyTimer = 15;
        readyPlayers.clear();
        answeredPlayers.clear();
        currentMatchQuestions.clear();

        // Сбрасываем очки матча, но оставляем людей в лобби
        playerScores.keySet().forEach(k -> playerScores.put(k, 0));
        broadcastLobbyState();
    }

    public void processAnswer(String sessionId, int answerIndex) {
        if (!isPlaying || currentQuestionObject == null) return;
        if (answerIndex < 0 || answerIndex >= currentQuestionObject.getOptions().size()) return;
        if (!sessionNicknames.containsKey(sessionId)) return;
        if (!answeredPlayers.add(sessionId)) return;

        if (answerIndex == currentQuestionObject.getCorrectOptionIndex()) {
            playerScores.put(sessionId, playerScores.getOrDefault(sessionId, 0) + 10);
        }
    }

    private void broadcastLobbyState() {
        // Отправляем текущее состояние лобби
        List<String> activeNicknames = new ArrayList<>(sessionNicknames.values());
        LobbyStateDTO state = new LobbyStateDTO(
                "LOBBY_STATE", activeNicknames, readyPlayers.size(), targetQuestionCount, lobbyTimer
        );
        messagingTemplate.convertAndSend("/topic/game", state);


        List<GlobalLeaderboardDTO.PlayerRow> globalTop = playerRepository.findTop10ByOrderByScoreDesc().stream()
                .map(p -> new GlobalLeaderboardDTO.PlayerRow(p.getNickname(), p.getScore()))
                .toList();
        messagingTemplate.convertAndSend("/topic/game", new GlobalLeaderboardDTO("GLOBAL_LEADERBOARD", globalTop));
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (sessionNicknames.containsKey(sessionId)) {
            String nickname = sessionNicknames.get(sessionId);
            log.info("Игрок отключился: {} (Сессия: {})", nickname, sessionId);

            // Удаляем игрока изо всех оперативных списков
            sessionNicknames.remove(sessionId);
            registeredSessions.remove(sessionId);
            readyPlayers.remove(sessionId);
            playerScores.remove(sessionId);
            answeredPlayers.remove(sessionId);

            // Если мы сейчас в лобби, обновляем список для оставшихся
            if (!isPlaying) {
                broadcastLobbyState();
            }
        }
    }
}