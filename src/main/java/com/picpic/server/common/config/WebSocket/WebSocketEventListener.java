package com.picpic.server.common.config.WebSocket;

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

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String roomId = headerAccessor.getFirstNativeHeader("roomId");
        Principal user = headerAccessor.getUser();

        if (roomId != null) {
            redisTemplate.opsForValue().set("sessionId:"+sessionId+":roomId", roomId);
            redisTemplate.opsForSet().add("room:"+roomId+":sessionIds", sessionId);

            if(user != null) {
                redisTemplate.opsForSet().add("room:"+roomId+":userNames", user.getName());
                redisTemplate.opsForValue().set("sessionId:"+sessionId+":userName", user.getName());
            }

            headerAccessor.getSessionAttributes().put("roomId", roomId);
            log.info("[STOMP] CONNECT roomId: {}, sessionId: {}", roomId, sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        String sessionId = headerAccessor.getSessionId();
        String roomId = redisTemplate.opsForValue().get("sessionId:"+sessionId+":roomId").toString();

        redisTemplate.delete("sessionId:"+sessionId+":roomId");
        redisTemplate.opsForSet().remove("room:"+roomId+":sessionIds", sessionId);

        if(user != null) {
            redisTemplate.opsForSet().remove("room:"+roomId+":userNames", user.getName());
            redisTemplate.delete("sessionId:"+sessionId+":userName");
        }
        log.info("[STOMP] DISCONNECT roomId: {}, sessionId: {}", roomId, sessionId);
    }
}
