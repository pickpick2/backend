package com.picpic.server.room.controller;

import com.picpic.server.common.response.ApiResponse;
import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.dto.CreatePhotoRoomResponseDto;
import com.picpic.server.room.service.usecase.CreateRoomUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreatePhotoRoomController {

    private final CreateRoomUseCase createRoomUseCase;

    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreatePhotoRoomResponseDto> createRoom(@AuthenticationPrincipal MemberPrincipalDetail memberDetail) {

        String createdRoomId = createRoomUseCase.createRoom(memberDetail);

        CreatePhotoRoomResponseDto response = CreatePhotoRoomResponseDto.builder()
                .roomId(createdRoomId)
                .build();

        return ApiResponse.success(response);
    }
}
