package com.picpic.server.room.controller;

import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.service.usecase.CreateRoomUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreatePhotoRoomController {

    private final CreateRoomUseCase createRoomUseCase;

    @PostMapping("/room")
    public String createRoom(@AuthenticationPrincipal MemberPrincipalDetail memberDetail) {
        return createRoomUseCase.createRoom(memberDetail.memberId());
    }
}
