package com.picpic.server.room.service;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picpic.server.common.response.WsResponse;
import com.picpic.server.room.dto.UpdateMemberRtcStateResponseDto;
import com.picpic.server.room.service.usecase.UpdateOVMemberStateUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateOVMemberStateService implements UpdateOVMemberStateUseCase {

	private final ObjectMapper objectMapper;

	private final SimpMessagingTemplate messagingTemplate;

	@Override
	public void updateMemberState(String payload) throws JsonProcessingException {

		Map<String, Object> payloadMap = parseToMap(payload);

		String event = payloadMap.get("event").toString();
		String roomId = payloadMap.get("sessionId").toString();

		UpdateMemberRtcStateResponseDto response;

		switch (event) {
			case "participantJoined":
				response = UpdateMemberRtcStateResponseDto.of(
					getMemberNickname(payloadMap),
					UpdateMemberRtcStateResponseDto.MemberRtcState.CONNECTED
				);

				messagingTemplate.convertAndSend("/topic/room/" + roomId, WsResponse.success("RTC_MEMBER_IN", response));
				break;

			case "participantLeft":
				response = UpdateMemberRtcStateResponseDto.of(
					getMemberNickname(payloadMap),
					UpdateMemberRtcStateResponseDto.MemberRtcState.DISCONNECTED
				);

				messagingTemplate.convertAndSend("/topic/room/" + roomId, WsResponse.success("RTC_MEMBER_OUT", response));
				break;
		}
	}

	private Map<String, Object> parseToMap(String payload) throws JsonProcessingException {
		Map<String, Object> map = objectMapper.readValue(payload, Map.class);

		return map;
	}

	private String getMemberNickname(Map<String, Object> payloadMap) throws JsonProcessingException {
		String serverData = (String) payloadMap.get("serverData");

		Map<String, Object> serverDataMap = objectMapper.readValue(serverData, Map.class);

		return serverDataMap.get("nickName").toString();
	}
}
