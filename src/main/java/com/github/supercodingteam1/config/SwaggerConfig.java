package com.github.supercodingteam1.config;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        Info info = new Info()
                .title("Super Coding Team 신발 쇼핑몰 API")
                .version("1.0")
                .description("신발 쇼핑몰 API에 대한 문서입니다.");

        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-AUTH-TOKEN");
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("X-AUTH-TOKEN");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("X-AUTH-TOKEN", apiKey))
                .addSecurityItem(securityRequirement)
                .info(info);
    }

    @Bean
    public GroupedOpenApi publicApi(){
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }

}
