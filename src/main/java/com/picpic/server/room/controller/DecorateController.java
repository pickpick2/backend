package com.picpic.server.room.controller;

import java.security.Principal;

import com.picpic.server.room.dto.*;
import com.picpic.server.room.dto.DecorateStickerRequestDTO;
import com.picpic.server.room.dto.DecorateStickerResponseDTO;
import com.picpic.server.room.dto.DecorateTextRequestDTO;
import com.picpic.server.room.dto.DecorateTextResponseDTO;
import com.picpic.server.room.dto.ws.DecorateStickerTransformRequestDTO;
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
        messagingTemplate.convertAndSend("/topic/room/" + penRequestDTO.roomId(), WsResponse.Success("DECOR_PEN", res));
    }

    @MessageMapping("/decor/sticker")
    public void sticker(Principal principal, DecorateStickerRequestDTO stickerRequestDTO) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateStickerResponseDTO res = decorateService.sticker(memberId,stickerRequestDTO);
        messagingTemplate.convertAndSend("/topic/room/" + stickerRequestDTO.roomId(), WsResponse.Success("DECOR_STICKER", res));
    }

    @MessageMapping("/decor/sticker/update")
    public void updateStickerPosition(Principal principal, UpdateStickerPositionRequestDTO dto) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateStickerResponseDTO res = decorateService.updateStickerPosition(memberId, dto);
        messagingTemplate.convertAndSend("/topic/room/" + dto.roomId(), WsResponse.Success("DECOR_STICKER_UPDATE", res));
    }

    @MessageMapping("/decor/sticker/remove")
    public void removeSticker(Principal principal, DeleteStickerRequestDTO reqDto) {
        Long memberId = Long.parseLong(principal.getName());
        DeletedStickerResponseDTO res = decorateService.deleteSticker(memberId, reqDto);
        messagingTemplate.convertAndSend("/topic/room/" + reqDto.roomId(), WsResponse.Success("DECOR_STICKER_REMOVE", res));
    }



    @MessageMapping("/decor/text")
    public void text(Principal principal, DecorateTextRequestDTO textRequestDTO) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateTextResponseDTO res = decorateService.putText(memberId,textRequestDTO);
        messagingTemplate.convertAndSend("/topic/room/" + textRequestDTO.roomId(),WsResponse.Success("DECOR_TEXT", res));
    }

    @MessageMapping("/decor/text/update")
    public void updateText(Principal principal, DecorateTextUpdateRequestDTO request) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateTextResponseDTO res = decorateService.updateText(memberId, request);
        messagingTemplate.convertAndSend("/topic/room/" + request.roomId(), WsResponse.Success("DECOR_TEXT_UPDATE", res));
    }

    @MessageMapping("/decor/text/move")
    public void moveText(Principal principal, DecorateTextMoveRequestDTO request) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateTextResponseDTO res = decorateService.moveText(memberId, request);
        messagingTemplate.convertAndSend("/topic/room/" + request.roomId(), WsResponse.Success("DECOR_TEXT_MOVE", res));
    }

    @MessageMapping("/decor/text/remove")
    public void removeText(Principal principal, DecorateTextDeleteRequestDTO request) {
        Long memberId = Long.parseLong(principal.getName());
        DeletedTextResponseDTO res = decorateService.removeText(memberId, request);
        messagingTemplate.convertAndSend("/topic/room/" + request.roomId(), WsResponse.Success("DECOR_TEXT_REMOVE", res));
    }

    @MessageMapping("/decor/sticker/transform")
    public void removeText(Principal principal, DecorateStickerTransformRequestDTO request) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateStickerResponseDTO res = decorateService.transformSticker(memberId, request);
        messagingTemplate.convertAndSend("/topic/room/" + request.roomId(), WsResponse.Success("DECOR_STICKER_TRANSFORM", res));
    }

    @MessageMapping("/decor/text/transform")
    public void removeText(Principal principal, DecorateTextTransformRequestDTO request) {
        Long memberId = Long.parseLong(principal.getName());
        DecorateTextResponseDTO res = decorateService.transformText(memberId, request);
        messagingTemplate.convertAndSend("/topic/room/" + request.roomId(), WsResponse.Success("DECOR_TEXT_TRANSFORM", res));
    }






}
