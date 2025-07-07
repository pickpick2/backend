package com.picpic.server.room.service;


import com.picpic.server.common.exception.ApiException;
import com.picpic.server.common.exception.ErrorCode;
import com.picpic.server.room.dto.*;
import com.picpic.server.room.entity.Member;
import com.picpic.server.room.entity.Participant;
import com.picpic.server.room.entity.Session;
import com.picpic.server.room.entity.Sticker;
import com.picpic.server.room.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecorateService {

    private final StickerRedisRepository stickerRedisRepository;
    private final StickerRepository stickerRepository;
    private final ParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final SessionRepository sessionRepository;
    private final TextRedisRepository textRedisRepository;
    private final PenRedisRepository penRedisRepository;



    public DecorateStickerResponseDTO sticker(Long memberId, DecorateStickerRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

        Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
        );

        Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_PARTICIPANT)
        );

        Sticker sticker = stickerRepository.findById(req.stickerId()).orElseThrow(
                () -> new ApiException(ErrorCode.NO_STICKER)
        );

        Long stickerInstanceId = stickerRedisRepository.saveSticker(req.sessionId(), req.stickerId(), memberId, req.points());

        return new DecorateStickerResponseDTO(
                stickerInstanceId,
                req.stickerId(),
                req.points().stream()
                        .map(p -> new DecorateStickerResponseDTO.Point(p.x(), p.y()))
                        .toList()
        );
    }


    public DecorateTextResponseDTO putText(Long memberId, DecorateTextRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

        Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
        );

        Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_PARTICIPANT)
        );

        String textBoxId = UUID.randomUUID().toString();

        textRedisRepository.saveText(
                req.sessionId(),
                textBoxId,
                req.text(),
                req.font(),
                req.color(),
                memberId,
                req.points()
        );

        DecorateTextResponseDTO res = new DecorateTextResponseDTO(
                textBoxId,
                req.text(),
                req.font(),
                req.color(),
                req.points().stream()
                        .map(p -> new DecorateTextResponseDTO.Point(p.x(), p.y()))
                        .toList()
        );

        return res;
    }

//    draw
    public DecoratePenResponseDTO draw(Long memberId, DecoratePenRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

        Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
        );

        Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_PARTICIPANT)
        );

        PenRedisDTO dto = new PenRedisDTO(
                req.tool(),
                req.color(),
                req.lineWidth(),
                req.points().stream()
                        .map(p -> new PenRedisDTO.Point(p.x(), p.y()))
                        .toList()
        );

        penRedisRepository.saveStroke(req.sessionId(), memberId, dto);

        DecoratePenResponseDTO res = new DecoratePenResponseDTO(
                req.tool().name().toLowerCase(), // "pen" or "eraser"
                req.color(),
                req.lineWidth(),
                req.points().stream()
                        .map(p -> new DecoratePenResponseDTO.Point(p.x(), p.y()))
                        .toList(),
                req.tool() // enum 그대로 응답
        );

        return res;
    }

    public DecorateStickerResponseDTO updateStickerPosition(Long memberId, UpdateStickerPositionRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

        Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
        );

        Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_PARTICIPANT)
        );

        stickerRedisRepository.updateStickerPosition(req.sessionId(), req.stickerInstanceId(), req.newPoints());

        return new DecorateStickerResponseDTO(
                req.stickerInstanceId(),
                req.stickerId(),
                req.newPoints().stream()
                        .map(p -> new DecorateStickerResponseDTO.Point(p.x(), p.y()))
                        .toList()
        );
    }

    public DeletedStickerResponseDTO deleteSticker(Long memberId, DeleteStickerRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

        Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
        );

        Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_PARTICIPANT)
        );

        stickerRedisRepository.deleteSticker(req.sessionId(), req.stickerInstanceId());

        return new DeletedStickerResponseDTO(req.stickerInstanceId());
    }

}

