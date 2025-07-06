package com.picpic.server.common.config.WebSocket.interceptor;

import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.service.usecase.RedisRoomQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomValidationInterceptor implements ChannelInterceptor {

    private final RedisRoomQueryUseCase redisRoomQueryUseCase;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            // TODO: Jwt 유틸 개발 이후, 이 부분에 토큰 안에 담긴 내용으로 principal 생성해야함
//            String token = accessor.getFirstNativeHeader("Authorization");
            String token = accessor.getFirstNativeHeader("X-Test-Member-Id");
            String roomId = accessor.getFirstNativeHeader("roomId");

            validateAlreadyEnter(token);

            if (token != null && roomId != null) {
                try {
                    validateRoomExist(roomId);
                    MemberPrincipalDetail principal = new MemberPrincipalDetail(
                            Long.parseLong(token),
                            "test nickname" //TODO
                    );
                    accessor.setUser(principal);
                } catch (Exception e) {
                    log.warn("[STOMP] CONNECT rejected - Room not found: {}", roomId);
                }
            }
        }

        return message;
    }

    private void validateRoomExist(String roomId) {
        if(!redisRoomQueryUseCase.exist(roomId)) {
            throw new RuntimeException("Room not found. : "+roomId);
        }
    }

    private void validateAlreadyEnter(String userId) {
        if(redisTemplate.opsForValue().get(userId+":roomId") != null) {
            throw new RuntimeException("You are already connected to a room.");
        }
    }
}
