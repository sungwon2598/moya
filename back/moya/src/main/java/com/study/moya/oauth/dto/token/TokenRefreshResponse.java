package com.study.moya.oauth.dto.token;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "토큰 갱신 결과")
public class TokenRefreshResponse {
    @Schema(description = "새로운 JWT 토큰")
    private String jwtToken;

    @Schema(description = "새로운 리프레시 토큰")
    private String refreshToken;
}