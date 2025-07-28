package com.picpic.server.frame.dto.ws;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CellSelectPayload {
	private Long frameId;
	private Integer cellIndex;
}
