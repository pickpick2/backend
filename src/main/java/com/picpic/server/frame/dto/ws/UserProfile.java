package com.picpic.server.frame.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 간단한 사용자 프로필 정보
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
	private Long userId;
	private String nickName;
	private String avatarUrl;
}