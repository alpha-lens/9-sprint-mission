package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // `/ws` : 서버 내부 엔드포인트 경로
    // 	CORS 설정 + SockJS 프로토콜 지원 추가 (fallback)
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
  }

  /* STOMP 메시지 라우팅 규칙 설정
   * 서버와 클라이언트가 메시지를 주고받을 때 `어떤 경로로 처리할지` 정의
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 클라이언트 --> 서버 prefix
    // ex) /pub/chatroom.create
    // @MessageMapping("chatroom.create") 으로 매핑됨
    registry.setApplicationDestinationPrefixes("/pub");

    // 서버 --> 클라이언트로 메시지 broadcasting 시 사용할 prefix
    // ex) /sub/chatroom/created
    // @SendTo("/sub/chatroom/created") 으로 클라이언트 구독 가능
    registry.enableSimpleBroker("/sub");
  }
}

