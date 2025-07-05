package com.picpic.server.room.controller;

import com.picpic.server.common.response.ApiResponse;
import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.dto.GetRoomMemberResponseDto;
import com.picpic.server.room.service.usecase.GetRoomMemberUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
