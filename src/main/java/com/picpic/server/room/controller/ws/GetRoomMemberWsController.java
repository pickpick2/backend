package com.picpic.server.room.controller.ws;

import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.picpic.server.common.response.WsResponse;
import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.dto.ws.GetRoomMemberWsResponseDto;
import com.picpic.server.room.service.usecase.GetRoomMemberUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GetRoomMemberWsController {

	private final GetRoomMemberUseCase getRoomMemberUseCase;

	@MessageMapping("/room/{roomId}/members")
	@SendToUser("/queue/room")
	public WsResponse<GetRoomMemberWsResponseDto> getRoom(
		@AuthenticationPrincipal MemberPrincipalDetail memberDetail,
		@DestinationVariable String roomId
	) {
		List<RoomMember> roomMember = getRoomMemberUseCase.getRoomMember(memberDetail.memberId(), roomId);

		return WsResponse.success("ROOM_MEMBER_LIST", GetRoomMemberWsResponseDto.from(roomMember));
	}
}
