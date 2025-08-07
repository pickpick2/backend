package com.picpic.server.room.service;

import org.springframework.stereotype.Service;

import com.picpic.server.common.exception.WsErrorCode;
import com.picpic.server.common.exception.WsException;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.service.usecase.CreateWebRtcRoomUseCase;
import com.picpic.server.room.service.usecase.RedisRoomQueryUseCase;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateWebRtcRoomService implements CreateWebRtcRoomUseCase {

	private final OpenVidu openvidu;

	private final RedisRoomQueryUseCase redisRoomQueryUseCase;

	@Override
	public String createRoom(Long creatorId, String roomId)	throws OpenViduJavaClientException, OpenViduHttpException {

		validateCreatorId(roomId, creatorId);

		SessionProperties.Builder builder = new SessionProperties.Builder();
		SessionProperties properties = builder.customSessionId(roomId).build();

		Session session = openvidu.createSession(properties);

		return session.getSessionId();
	}

	private void validateCreatorId (String roomId, Long requestMemberId) {
		RoomMember actualCreator = redisRoomQueryUseCase.getCreator(roomId);

		if(!actualCreator.getMemberId().equals(requestMemberId)) {
			throw new WsException(WsErrorCode.ACCESS_DENIED_CREATOR_REQUIRED);
		}
	}
}
