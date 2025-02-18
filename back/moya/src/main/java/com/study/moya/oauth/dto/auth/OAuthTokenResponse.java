package com.study.moya.oauth.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "Google ID 토큰 응답")
public class OAuthTokenResponse {
    @Schema(description = "OAuth 액세스 토큰")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "토큰 만료 시간(초)")
    @JsonProperty("expires_in")
    private Long expiresIn;

    @Schema(description = "리프레시 토큰")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Schema(description = "토큰 스코프")
    @JsonProperty("scope")
    private String scope;

    @Schema(description = "토큰 타입", example = "Bearer")
    @JsonProperty("token_type")
    private String tokenType;

    @Schema(description = "ID 토큰")
    @JsonProperty("id_token")
    private String idToken;
}