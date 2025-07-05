package com.picpic.server.common.config.WebSocket;

import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.service.usecase.RedisRoomCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisRoomCommandUseCase redisRoomCommand;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String roomId = headerAccessor.getFirstNativeHeader("roomId");
        Principal user = headerAccessor.getUser();

        MemberPrincipalDetail memberPrincipal
                = user instanceof MemberPrincipalDetail ? (MemberPrincipalDetail) user : null;

        if(roomId == null) {
            throw new RuntimeException("Room not found. : " + roomId);
        }

        if(memberPrincipal == null) {
            throw new RuntimeException("Member information is missing.");
        }

        redisRoomCommand.addMember(roomId, memberPrincipal);

        headerAccessor.getSessionAttributes().put("roomId", roomId);

        redisTemplate.opsForValue().set(memberPrincipal.getName()+":roomId", roomId);

        log.info("[STOMP] CONNECT roomId: {}, sessionId: {}", roomId, sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        String sessionId = headerAccessor.getSessionId();
        String roomId = redisTemplate.opsForValue().get(user.getName() + ":roomId").toString();

        redisRoomCommand.subtractMember(roomId, Long.valueOf(user.getName()));
        redisTemplate.delete(user.getName()+":roomId");

        log.info("[STOMP] DISCONNECT roomId: {}, sessionId: {}", roomId, sessionId);
    }
}
