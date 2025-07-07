package com.picpic.server.common.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI picpicOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PicPic API")
                        .description("픽픽 프로젝트 API 문서")
                        .version("v1.0")
                        .contact(new Contact().name("PicPic Team").email("minjumost@naver.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub Repository")
                        .url("나중에 추가"));
    }
}
