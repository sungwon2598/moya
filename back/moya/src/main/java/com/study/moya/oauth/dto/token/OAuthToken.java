package com.study.moya.oauth.dto.token;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "OAuth 액세스 토큰 정보")
public class OAuthToken {
    @Schema(description = "OAuth 액세스 토큰")
    private String oAuthAccessToken;

    @Schema(description = "리프레시 토큰")
    private String refreshToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    @Schema(description = "토큰 만료 시간(초)")
    private Long expiresIn;

    @Builder
    public OAuthToken(String oAuthAccessToken, String refreshToken, String tokenType, Long expiresIn) {
        this.oAuthAccessToken = oAuthAccessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }
}