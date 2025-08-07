package com.picpic.server.room.service.usecase;

import com.picpic.server.common.auth.MemberPrincipalDetail;

import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;

public interface CreateWebRtcTokenUseCase {
	String createToken(String roomId, MemberPrincipalDetail memberDetail) throws OpenViduJavaClientException, OpenViduHttpException;
}
