package com.picpic.server.common.config.WebSocket;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.picpic.server.common.exception.WsErrorCode;
import com.picpic.server.common.exception.WsException;
import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.service.usecase.RedisRoomCommandUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisRoomCommandUseCase redisRoomCommand;
	private final SimpMessagingTemplate messagingTemplate;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectEvent event) {

		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = headerAccessor.getSessionId();
		String roomId = headerAccessor.getFirstNativeHeader("roomId");
		Principal user = headerAccessor.getUser();

		MemberPrincipalDetail memberPrincipal
			= user instanceof MemberPrincipalDetail ? (MemberPrincipalDetail)user : null;

		if (roomId == null) {
			throw new WsException(WsErrorCode.NOT_FOUND_ROOM);

		}

		if (memberPrincipal == null) {
			throw new WsException(WsErrorCode.MISSING_MEMBER_INFO);
		}

		redisRoomCommand.addMember(roomId, memberPrincipal);

		headerAccessor.getSessionAttributes().put("roomId", roomId);

		redisTemplate.opsForValue().set(memberPrincipal.getName() + ":roomId", roomId);

		sendMemberEventToRoom(roomId, memberPrincipal.nickName(), "ENTER_MEMBER");

		log.info("[STOMP] CONNECT roomId: {}, sessionId: {}, memberId: {}", roomId, sessionId, user.getName());
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = headerAccessor.getSessionId();
		Principal user = headerAccessor.getUser();

		if(user == null) {
			return;
		}

		String roomId = redisTemplate.opsForValue().get(user.getName() + ":roomId").toString();

		MemberPrincipalDetail memberPrincipal
			= user instanceof MemberPrincipalDetail ? (MemberPrincipalDetail)user : null;

		redisRoomCommand.subtractMember(roomId, Long.valueOf(user.getName()));
		redisTemplate.delete(user.getName() + ":roomId");

		sendMemberEventToRoom(roomId, memberPrincipal.nickName(), "LEAVE_MEMBER");

		log.info("[STOMP] DISCONNECT roomId: {}, sessionId: {}, member: {}", roomId, sessionId, user.getName());
	}

	private void sendMemberEventToRoom(String roomId, String nickname, String notingType) {

		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();

		data.put("nickname", nickname);

		response.put("type", notingType);
		response.put("data", data);

		messagingTemplate.convertAndSend("/topic/room/" + roomId, response);
	}
}
