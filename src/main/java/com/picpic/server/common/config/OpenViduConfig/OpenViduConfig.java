package com.picpic.server.common.config.OpenViduConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.openvidu.java.client.OpenVidu;

@Configuration
public class OpenViduConfig {

	@Value("${OPENVIDU_URL}")
	private String OPENVIDU_URL;

	@Value("${OPENVIDU_SECRET}")
	private String OPENVIDU_SECRET;

	@Bean
	public OpenVidu openvidu() {
		return new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
	}
}
