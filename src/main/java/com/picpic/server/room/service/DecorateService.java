package com.picpic.server.room.service;


import com.picpic.server.common.exception.ApiException;
import com.picpic.server.common.exception.ErrorCode;
import com.picpic.server.member.repository.MemberRepository;
import com.picpic.server.room.dto.*;
import com.picpic.server.member.entity.Member;
import com.picpic.server.room.dto.ws.DecorateStickerTransformRequestDTO;
import com.picpic.server.room.entity.Participant;
import com.picpic.server.room.entity.Session;
import com.picpic.server.room.entity.Sticker;
import com.picpic.server.room.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SimpMessagingTemplate messagingTemplate;

    public DecorateStickerResponseDTO sticker(Long memberId, DecorateStickerRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

//		Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
//			() -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
//		);
//
//		Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
//			() -> new ApiException(ErrorCode.NOT_PARTICIPANT)
//		);

        Sticker sticker = stickerRepository.findById(req.stickerId()).orElseThrow(
                () -> new ApiException(ErrorCode.NO_STICKER)
        );

        Long stickerInstanceId = stickerRedisRepository.saveSticker(
                req.roomId(),
                req.stickerId(),
                memberId,
                req.x(),
                req.y(),
                req.width(),
                req.height()
        );

        return new DecorateStickerResponseDTO(
                stickerInstanceId,
                req.stickerId(),
                req.x(),
                req.y(),
                req.width(),
                req.height(),
                1.0,
                0

        );
    }

    public DecorateTextResponseDTO putText(Long memberId, DecorateTextRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

//		Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
//			() -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
//		);
//
//		Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
//			() -> new ApiException(ErrorCode.NOT_PARTICIPANT)
//		);

        String textBoxId = UUID.randomUUID().toString();

        textRedisRepository.saveText(
                req.roomId(),
                textBoxId,
                req.text(),
                req.font(),
                req.fontSize(),
                req.color(),
                req.x(),
                req.y(),
                memberId

        );

        DecorateTextResponseDTO res = new DecorateTextResponseDTO(
                textBoxId,
                req.text(),
                req.font(),
                req.color(),
                req.x(),
                req.y(),
                req.fontSize(),
                0,
                1.0

        );

        return res;
    }

    //    draw
    public DecoratePenResponseDTO draw(Long memberId, DecoratePenRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

//        Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
//        );

//        Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_PARTICIPANT)
//        );

        PenRedisDTO dto = new PenRedisDTO(
                req.tool(),
                req.color(),
                req.strokeWidth(),
                req.x(),
                req.y()
        );

        penRedisRepository.saveStroke(req.roomId(), memberId, dto);

        DecoratePenResponseDTO res = new DecoratePenResponseDTO(
                req.tool(),
                req.color(),
                req.strokeWidth(),
                req.x(),
                req.y()
        );

        return res;
    }

    public DecorateStickerResponseDTO updateStickerPosition(Long memberId, UpdateStickerPositionRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

//        Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
//        );
//
//        Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_PARTICIPANT)
//        );

        StickerRedisDTO updatedSticker = stickerRedisRepository.updateStickerPosition(
                req.roomId(),
                req.stickerInstanceId(),
                req.x(),
                req.y()
        );

        return new DecorateStickerResponseDTO(
                updatedSticker.stickerInstanceId(),
                updatedSticker.stickerId(),
                updatedSticker.x(),
                updatedSticker.y(),
                updatedSticker.width(),
                updatedSticker.height(),
                updatedSticker.scale(),
                updatedSticker.rotate()
        );
    }

    public DeletedStickerResponseDTO deleteSticker(Long memberId, DeleteStickerRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

//        Session session = sessionRepository.findById(req.roomId()).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
//        );
//
//        Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_PARTICIPANT)
//        );

        stickerRedisRepository.deleteSticker(req.roomId(), req.stickerInstanceId());

        return new DeletedStickerResponseDTO(
                req.stickerInstanceId());
    }

    @Transactional
    public DecorateTextResponseDTO updateText(Long memberId, DecorateTextUpdateRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

//        Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
//        );
//
//        Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_PARTICIPANT)
//        );


        TextRedisDTO existing = textRedisRepository.findText(req.roomId(), req.textBoxId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_TEXT));

        TextRedisDTO updated = new TextRedisDTO(
                req.textBoxId(),
                req.newText(),
                req.newFont(),
                req.newFontSize(),
                req.newColor(),
                existing.x(),
                existing.y(),
                existing.rotate(),
                existing.scale()

        );

        textRedisRepository.updateText(req.roomId(), updated);


        return new DecorateTextResponseDTO(
                updated.textBoxId(),
                updated.text(),
                updated.font(),
                updated.color(),
                updated.x(),
                updated.y(),
                updated.fontSize(),
                updated.rotate(),
                updated.scale()
        );
    }

    @Transactional
    public DecorateTextResponseDTO moveText(Long memberId, DecorateTextMoveRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

//        Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
//        );
//
//        Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_PARTICIPANT)
//        );


        TextRedisDTO existing = textRedisRepository.findText(req.roomId(), req.textBoxId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_TEXT));

        textRedisRepository.updateTextPosition(req.roomId(), req.textBoxId(), req.x(), req.y());


        return new DecorateTextResponseDTO(
                existing.textBoxId(),
                existing.text(),
                existing.font(),
                existing.color(),
                req.x(),
                req.y(),
                existing.fontSize(),
                existing.rotate(),
                existing.scale()

        );
    }

    @Transactional
    public DeletedTextResponseDTO removeText(Long memberId, DecorateTextDeleteRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

//        Session session = sessionRepository.findById(req.sessionId()).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_FOUND_SESSION)
//        );
//
//        Participant participant = participantRepository.findBySessionAndMember(session, member).orElseThrow(
//                () -> new ApiException(ErrorCode.NOT_PARTICIPANT)
//        );

        TextRedisDTO existing = textRedisRepository.findText(req.roomId(), req.textBoxId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_TEXT));

        textRedisRepository.deleteText(req.roomId(), req.textBoxId());

        // 4. 응답용 객체 반환 (삭제됐지만 어떤 게 삭제됐는지 알려줌)
        return new DeletedTextResponseDTO(req.textBoxId());
    }

    public DecorateStickerResponseDTO transformSticker(Long memberId, DecorateStickerTransformRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

        StickerRedisDTO transformSticker = stickerRedisRepository.transformSticker(
                req.roomId(),
                req.stickerInstanceId(),
                req.scale(),
                req.rotate()
        );

        return new DecorateStickerResponseDTO(
                transformSticker.stickerInstanceId(),
                transformSticker.stickerId(),
                transformSticker.x(),
                transformSticker.y(),
                transformSticker.width(),
                transformSticker.height(),
                transformSticker.scale(),
                transformSticker.rotate()
        );
    }

    public DecorateTextResponseDTO transformText(Long memberId, DecorateTextTransformRequestDTO req) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

        TextRedisDTO existing = textRedisRepository.findText(req.roomId(), req.textBoxId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_TEXT));

        textRedisRepository.updateTextTransform(req.roomId(), req.textBoxId(), req.scale(), req.rotate());

        return new DecorateTextResponseDTO(
                existing.textBoxId(),
                existing.text(),
                existing.font(),
                existing.color(),
                existing.x(),
                existing.y(),
                existing.fontSize(),
                req.rotate(),
                req.scale()

        );
    }

    public DecoreStartResponseDTO startDeocorate(Long memberId, Long roomId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
        );

//		session.decoration();

        DecoreStartResponseDTO res = new DecoreStartResponseDTO(Instant.now(), 300);
        log.info("꾸미기모드 시작");

        return res;


    }
}





