package com.picpic.server.common.config.WebSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.picpic.server.common.config.WebSocket.interceptor.RoomValidationInterceptor;
import com.picpic.server.common.config.WebSocket.interceptor.StompExceptionHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

	private final RoomValidationInterceptor roomValidationInterceptor;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
			.setErrorHandler(new StompExceptionHandler())
			.addEndpoint("/wss/connection")
			.setAllowedOriginPatterns("*");

		registry
			.setErrorHandler(new StompExceptionHandler())
			.addEndpoint("/wss/connection")
			.setAllowedOriginPatterns("*")
			.withSockJS();

	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic", "/queue"); // /queue 추가
		registry.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {

		registration.interceptors(
			roomValidationInterceptor
		);

	}
}
