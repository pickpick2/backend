package com.picpic.server.room.service.usecase;

import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;

public interface CreateWebRtcRoomUseCase {
	String createRoom(Long creatorId, String roomId) throws OpenViduJavaClientException, OpenViduHttpException;
}
