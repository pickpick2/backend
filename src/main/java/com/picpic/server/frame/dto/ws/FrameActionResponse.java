package com.picpic.server.frame.dto.ws;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FrameActionResponse {
	private FrameMessageType type;
	private List<UserProfile> profiles;
}
