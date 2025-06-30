package com.picpic.server.room.controller;

import java.security.Principal;

import com.picpic.server.room.dto.DecorateStickerRequestDTO;
import com.picpic.server.room.dto.DecorateStickerResponseDTO;
import com.picpic.server.room.service.DecorateService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DecorateController {

    private final DecorateService decorateService;
    private final SimpMessagingTemplate messagingTemplate;

//    @MessageMapping("/decor/start")
//    public void decorateStart(Principal principal, DecorateStartRequestDTO decorateStartRequestDTO) {
//        Long memberId = Long.parseLong(principal.getName());
//        DecorateStartResponseDTO res ;
//    }
//
//    @MessageMapping("/decor/pen")
//    public void stroke(Principal principal) {
//        Long memberId = Long.parseLong(principal.getName());
//    }

    @MessageMapping("/decor/sticker")
    public void sticker(Principal principal, DecorateStickerRequestDTO stickerRequestDTO) {

        Long memberId = Long.parseLong(principal.getName());
        DecorateStickerResponseDTO res = decorateService.sticker(memberId,stickerRequestDTO);
        messagingTemplate.convertAndSend("/broadcast/" + stickerRequestDTO.sessionCode(), res);
    }

//    @MessageMapping("/decor/text")
//    public void text(Principal principal) {
//        Long memberId = Long.parseLong(principal.getName());
//    }

}
