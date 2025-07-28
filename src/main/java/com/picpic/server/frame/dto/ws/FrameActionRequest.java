package com.picpic.server.frame.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 클라이언트 → 서버 : 프레임 선택/투표 요청
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FrameActionRequest {
	private Long frameId;
	private Long userId;
}