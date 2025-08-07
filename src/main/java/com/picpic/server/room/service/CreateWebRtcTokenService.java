package com.picpic.server.room.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picpic.server.common.auth.MemberPrincipalDetail;
import com.picpic.server.common.exception.WsErrorCode;
import com.picpic.server.common.exception.WsException;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.service.usecase.CreateWebRtcTokenUseCase;
import com.picpic.server.room.service.usecase.RedisRoomQueryUseCase;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateWebRtcTokenService implements CreateWebRtcTokenUseCase {

	private final OpenVidu openvidu;

	private final RedisRoomQueryUseCase redisRoomQueryUseCase;

	private final ObjectMapper om;

	@Override
	public String createToken(String roomId, MemberPrincipalDetail memberDetail) throws OpenViduJavaClientException, OpenViduHttpException {
		Session session = openvidu.getActiveSession(roomId);

		if (session == null) {
			throw new WsException(WsErrorCode.NOT_FOUND_ROOM);
		}

		validateRoomMember(roomId, memberDetail.memberId());

		ConnectionProperties.Builder connectionBuilder = new ConnectionProperties.Builder();

		try {
			connectionBuilder.data(om.writeValueAsString(memberDetail));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		ConnectionProperties properties = connectionBuilder.build();
		Connection connection = session.createConnection(properties);
		return connection.getToken();
	}

	private void validateRoomMember(String roomId, Long memberId) {
		List<RoomMember> roomMembers = redisRoomQueryUseCase.searchMember(roomId);

		long count = roomMembers.stream().filter(member ->
			member.getMemberId().equals(memberId)
		).count();

		if(count == 0) {
			throw new WsException(WsErrorCode.USER_NOT_IN_ROOM);
		}
	}
}
