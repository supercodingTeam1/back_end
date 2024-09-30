package com.github.supercodingteam1.config.auth.dto;




import lombok.*;


@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TokenDTO {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpires;

    public static TokenDTO of(String accessToken, String refreshToken , Long accessTokenExpires) {
        return TokenDTO.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpires(accessTokenExpires)
                .build();
    }

}
