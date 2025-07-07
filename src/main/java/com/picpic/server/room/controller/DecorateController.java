package com.picpic.server.room.controller;

import java.security.Principal;

import com.picpic.server.room.dto.*;
import com.picpic.server.room.dto.DecorateStickerRequestDTO;
import com.picpic.server.room.dto.DecorateStickerResponseDTO;
import com.picpic.server.room.dto.DecorateTextRequestDTO;
import com.picpic.server.room.dto.DecorateTextResponseDTO;
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

    @MessageMapping("/decor/pen")
    public void stroke(Principal principal, DecoratePenRequestDTO penRequestDTO) {
        Long memberId = Long.parseLong(principal.getName());
        DecoratePenResponseDTO res = decorateService.draw(memberId,penRequestDTO);
        messagingTemplate.convertAndSend("/topic/" + penRequestDTO.sessionCode(), res);
    }

    @MessageMapping("/decor/sticker")
    public void sticker(Principal principal, DecorateStickerRequestDTO stickerRequestDTO) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateStickerResponseDTO res = decorateService.sticker(memberId,stickerRequestDTO);
        messagingTemplate.convertAndSend("/topic/" + stickerRequestDTO.sessionCode(), res);
    }

    @MessageMapping("/decor/sticker/update")
    public void updateStickerPosition(Principal principal, UpdateStickerPositionRequestDTO dto) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateStickerResponseDTO res = decorateService.updateStickerPosition(memberId, dto);
        messagingTemplate.convertAndSend("/topic/" + dto.sessionCode(), res);
    }

    @MessageMapping("/decor/sticker/remove")
    public void removeSticker(Principal principal, DeleteStickerRequestDTO reqDto) {
        Long memberId = Long.parseLong(principal.getName());
        DeletedStickerResponseDTO res = decorateService.deleteSticker(memberId, reqDto);
        messagingTemplate.convertAndSend("/topic/" + reqDto.sessionCode(), res);
    }



    @MessageMapping("/decor/text")
    public void text(Principal principal, DecorateTextRequestDTO textRequestDTO) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateTextResponseDTO res = decorateService.putText(memberId,textRequestDTO);
        messagingTemplate.convertAndSend("/topic/" + textRequestDTO.sessionCode(), res);
    }

    @MessageMapping("/decor/text/update")
    public void updateText(Principal principal, DecorateTextUpdateRequestDTO request) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateTextResponseDTO res = decorateService.updateText(memberId, request);
        messagingTemplate.convertAndSend("/topic/" + request.sessionCode(), res);
    }

    @MessageMapping("/decor/text/move")
    public void moveText(Principal principal, DecorateTextMoveRequestDTO request) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateTextResponseDTO res = decorateService.moveText(memberId, request);
        messagingTemplate.convertAndSend("/topic/" + request.sessionCode(), res);
    }



}
