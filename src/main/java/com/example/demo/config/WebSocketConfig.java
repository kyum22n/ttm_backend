package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    // 프론트: new SockJS('/ws')
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app"); // 서버 수신 prefix
    registry.enableSimpleBroker("/topic", "/queue");   // 서버 발행 prefix
    // 1:1 룸 브로드캐스트: /topic/chat.{roomId}
  }
}
