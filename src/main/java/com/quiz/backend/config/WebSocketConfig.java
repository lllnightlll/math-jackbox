package com.quiz.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker //  STOMP
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Эндпоинт, для фронта фронтендер (ws://localhost:8080/ws)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // Разрешаем фронту подключаться с других портов

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Топики, куда СЕРВЕР будет отправлять сообщения (фронт на них подписывается)
        registry.enableSimpleBroker("/topic");

        // Префикс для сообщений, которые ФРОНТ отправляет на сервер
        registry.setApplicationDestinationPrefixes("/app");
    }
}