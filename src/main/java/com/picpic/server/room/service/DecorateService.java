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


    @Transactional
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

        stickerRedisRepository.saveSticker(req.sessionId(), req.stickerId(), memberId, req.points());

        DecorateStickerResponseDTO res = new DecorateStickerResponseDTO(
                req.stickerId(),
                req.points().stream()
                        .map(p -> new DecorateStickerResponseDTO.Point(p.x(), p.y()))
                        .toList()
        );

        return res;

    }

    @Transactional
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

        // Redis에 저장
        TextRedisDTO dto = new TextRedisDTO(
                req.text(),
                req.font(),
                req.color(),
                req.points().stream()
                        .map(p -> new TextRedisDTO.Point(p.x(), p.y()))
                        .toList()
        );
        textRedisRepository.saveText(req.sessionId(), req.text(), req.font(), req.color(), memberId, req.points());

        DecorateTextResponseDTO res = new DecorateTextResponseDTO(
                dto.text(),
                dto.font(),
                dto.color(),
                dto.points().stream()
                        .map(p -> new DecorateTextResponseDTO.Point(p.x(), p.y()))
                        .toList()
        );

        return res;
    }

}

