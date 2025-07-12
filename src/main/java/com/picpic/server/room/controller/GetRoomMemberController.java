package com.picpic.server.room.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.picpic.server.common.auth.MemberPrincipalDetail;
import com.picpic.server.common.response.ApiResponse;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.dto.GetRoomMemberResponseDto;
import com.picpic.server.room.service.usecase.GetRoomMemberUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GetRoomMemberController {

	private final GetRoomMemberUseCase getRoomMemberUseCase;

	@GetMapping("/room/{roomId}/member")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<GetRoomMemberResponseDto> getRoom(
		@AuthenticationPrincipal MemberPrincipalDetail memberDetail,
		@PathVariable String roomId
	) {
		List<RoomMember> roomMember = getRoomMemberUseCase.getRoomMember(memberDetail.memberId(), roomId);

		return ApiResponse.success(GetRoomMemberResponseDto.from(roomMember));
	}
}
