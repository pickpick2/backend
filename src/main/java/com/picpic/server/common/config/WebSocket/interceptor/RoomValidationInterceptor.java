package com.picpic.server.common.config.WebSocket.interceptor;

import static com.picpic.server.common.exception.WsErrorCode.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.picpic.server.common.auth.JwtTokenProvider;
import com.picpic.server.common.exception.ApiException;
import com.picpic.server.common.exception.ErrorCode;
import com.picpic.server.common.exception.WsException;
import com.picpic.server.member.entity.Member;
import com.picpic.server.room.service.usecase.RedisRoomQueryUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.picpic.server.common.auth.MemberPrincipalDetail;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomValidationInterceptor implements ChannelInterceptor {

	private final RedisRoomQueryUseCase redisRoomQueryUseCase;
	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
           	String token = accessor.getFirstNativeHeader("Authorization");
            String roomId = accessor.getFirstNativeHeader("roomId");

			token = jwtTokenProvider.resolveToken(token);

			if (token == null || roomId == null) {
				log.warn("[STOMP] CONNECT rejected - Missing token or roomId");
				return null;
			}

			try {
				validateToken(token);

				String userId = String.valueOf(jwtTokenProvider.getMemberId(token));
				String nickname = jwtTokenProvider.getNickname(token);
				Member.Role role = jwtTokenProvider.getRole(token);

				validateAlreadyEnter(userId);
				validateRoomExist(roomId);
				validateRoomCapacity(roomId);

				MemberPrincipalDetail principal = new MemberPrincipalDetail(
					Long.parseLong(userId),
					nickname,
					role
				);

                accessor.setUser(principal);

                log.info("[STOMP] CONNECT approved - userId: {}, roomId: {}", userId, roomId);
            }  catch (WsException e) {
                log.error("[STOMP] CONNECT failed: {}", e.getMessage(), e);
				throw new MessagingException(e.getMessage(), e);
            }
        }

        return message;
    }

    private void validateRoomExist(String roomId) {
        if(!redisRoomQueryUseCase.exist(roomId)) {
            throw new WsException(NOT_FOUND_ROOM);
        }
    }

    private void validateAlreadyEnter(String userId) {
        if(redisTemplate.opsForValue().get(userId+":roomId") != null) {
            throw new WsException(ALREADY_CONNECTED);
        }
    }

    private void validateRoomCapacity(String roomId) {

        Integer roomCapacity = redisRoomQueryUseCase.getRoomCapacity(roomId);
        Integer currentMemberNum = redisRoomQueryUseCase.searchMember(roomId).size();

        if(currentMemberNum >= roomCapacity ) {
            throw new WsException(EXCEED_ROOM_CAPACITY);
        }
    }

	private void validateToken(String token) {
		try {
			jwtTokenProvider.validateToken(token);
		} catch (ApiException e) {
			if(e.getErrorCode() == ErrorCode.EXPIRED_TOKEN) {
				throw new WsException(EXPIRED_TOKEN);
			}
			if(e.getErrorCode() == ErrorCode.UNAVAILABLE_TOKEN) {
				throw new WsException(UNAVAILABLE_TOKEN);
			}
		}
	}
}
