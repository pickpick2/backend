package com.picpic.server.common.config.WebSocket.interceptor;

import com.picpic.server.room.entity.RoomEntity;
import com.picpic.server.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomValidationInterceptor implements ChannelInterceptor {

    private final RoomRepository roomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            String roomId = accessor.getFirstNativeHeader("roomId");

            if (roomId != null) {
                try {
                    validateRoomExist(roomId);
                } catch (Exception e) {
                    log.warn("[STOMP] CONNECT rejected - Room not found: {}", roomId);
                    throw new IllegalArgumentException("존재하지 않는 방입니다: " + roomId);
                }
            }
        }

        return message;
    }

    private void validateRoomExist(String roomId) {

        Optional<RoomEntity> roomEntity = roomRepository.findById(roomId);

        if(roomEntity.isEmpty()) {
            throw new RuntimeException("Room is no exsit: "+roomId);
        }
    }
}
