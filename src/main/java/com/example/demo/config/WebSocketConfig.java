package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // WebSocket 메시지 브로커 기능 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 WebSocket 서버에 연결할 주소를 정의 (ws://localhost:8080/ws)
        registry.addEndpoint("/ws")
                // 다른 도메인(프론트엔드)에서도 접속 가능하게 허용
                .setAllowedOriginPatterns("*")
                // 브라우저가 WebSocket을 지원하지 않아도 연결되도록 SockJS 사용
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 서버로 메시지를 보낼 때 붙이는 prefix (예: /app/hello)
        registry.setApplicationDestinationPrefixes("/app");
        // 서버가 클라이언트에게 메시지를 보낼 때 사용할 prefix
        // 예: 클라이언트는 /topic/room1 을 구독하면 해당 방의 메시지를 받을 수 있음
        registry.enableSimpleBroker("/topic");
    }
}

