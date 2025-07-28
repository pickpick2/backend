package com.picpic.server.frame.controller.ws;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.picpic.server.common.config.WebSocket.WebSocketMessageBrokerConfig;
import com.picpic.server.common.config.redis.RedisConfig;
import com.picpic.server.frame.dto.ws.FrameActionRequest;
import com.picpic.server.frame.dto.ws.FrameMessageType;
import com.picpic.server.frame.dto.ws.FrameWsMessage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({WebSocketMessageBrokerConfig.class, RedisConfig.class})
class FrameWsIntegrationTest {

	@Value("${local.server.port}")
	private int port;

	private WebSocketStompClient stompClient;
	private String wsUrl;

	@BeforeEach
	void setup() {
		stompClient = new WebSocketStompClient(new StandardWebSocketClient());
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		wsUrl = "ws://localhost:" + port + "/wss/connection";
	}

	@Test
	void whenSendFrameVoteMessage_thenReceiveBroadcast() throws Exception {
		String roomId = "room1";

		// 1) WebSocket 핸드쉐이크에 보낼 HTTP 헤더 준비
		WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
		handshakeHeaders.add("Authorization", "Bearer invalid");
		handshakeHeaders.add("roomId", roomId);

		// 2) STOMP 연결 시에 헤더를 함께 전달
		ListenableFuture<StompSession> fut =
			stompClient.connect(wsUrl, handshakeHeaders, new StompSessionHandlerAdapter() {
			});
		StompSession session = fut.get(1, TimeUnit.SECONDS);

		BlockingQueue<FrameWsMessage<?>> queue = new LinkedBlockingDeque<>();

		session.subscribe("/topic/room/" + roomId + "/frames", new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return FrameWsMessage.class;
			}

			@Override
			@SuppressWarnings("unchecked")
			public void handleFrame(StompHeaders headers, Object payload) {
				queue.add((FrameWsMessage<?>)payload);
			}
		});

		FrameActionRequest req = new FrameActionRequest(123L, 7L);
		FrameWsMessage<FrameActionRequest> wrapper =
			new FrameWsMessage<>(FrameMessageType.VOTE, "req-uuid-1", req);

		session.send("/app/frame/action", wrapper);

		FrameWsMessage<?> response = queue.poll(2, TimeUnit.SECONDS);
		assertThat(response).isNotNull();
		assertThat(response.getType()).isEqualTo(FrameMessageType.VOTE);
		assertThat(response.getRequestId()).isEqualTo("req-uuid-1");
	}
}