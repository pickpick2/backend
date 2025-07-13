package com.picpic.server.room.controller;

import java.security.Principal;

import com.picpic.server.room.dto.*;
import com.picpic.server.room.dto.DecorateStickerRequestDTO;
import com.picpic.server.room.dto.DecorateStickerResponseDTO;
import com.picpic.server.room.dto.DecorateTextRequestDTO;
import com.picpic.server.room.dto.DecorateTextResponseDTO;
import com.picpic.server.room.service.DecorateService;
import org.apache.tomcat.websocket.WsHandshakeResponse;
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
        messagingTemplate.convertAndSend("/app/" + penRequestDTO.roomId(), res);
    }

    @MessageMapping("/decor/sticker")
    public void sticker(Principal principal, DecorateStickerRequestDTO stickerRequestDTO) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateStickerResponseDTO res = decorateService.sticker(memberId,stickerRequestDTO);
        messagingTemplate.convertAndSend("/app/" + stickerRequestDTO.roomId(), res);
    }

    @MessageMapping("/decor/sticker/update")
    public void updateStickerPosition(Principal principal, UpdateStickerPositionRequestDTO dto) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateStickerResponseDTO res = decorateService.updateStickerPosition(memberId, dto);
        messagingTemplate.convertAndSend("/app/" + dto.roomId(), res);
    }

    @MessageMapping("/decor/sticker/remove")
    public void removeSticker(Principal principal, DeleteStickerRequestDTO reqDto) {
        Long memberId = Long.parseLong(principal.getName());
        DeletedStickerResponseDTO res = decorateService.deleteSticker(memberId, reqDto);
        messagingTemplate.convertAndSend("/app/" + reqDto.roomId(), res);
    }



    @MessageMapping("/decor/text")
    public void text(Principal principal, DecorateTextRequestDTO textRequestDTO) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateTextResponseDTO res = decorateService.putText(memberId,textRequestDTO);
        messagingTemplate.convertAndSend("/app/" + textRequestDTO.roomId(),res);
    }

    @MessageMapping("/decor/text/update")
    public void updateText(Principal principal, DecorateTextUpdateRequestDTO request) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateTextResponseDTO res = decorateService.updateText(memberId, request);
        messagingTemplate.convertAndSend("/app/" + request.roomId(), res);
    }

    @MessageMapping("/decor/text/move")
    public void moveText(Principal principal, DecorateTextMoveRequestDTO request) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateTextResponseDTO res = decorateService.moveText(memberId, request);
        messagingTemplate.convertAndSend("/app/" + request.roomId(), res);
    }

    @MessageMapping("/decor/text/remove")
    public void removeText(Principal principal, DecorateTextDeleteRequestDTO request) {
        Long memberId = Long.parseLong(principal.getName());
        DeletedTextResponseDTO res = decorateService.removeText(memberId, request);
        messagingTemplate.convertAndSend("/app/" + request.roomId(), res);
    }





}
