package com.study.moya.oauth.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OAuth 로그인 요청")
public class OAuthLoginRequest {
    @Schema(description = "OAuth 인증 코드", required = true)
    private String authCode;

    @Schema(description = "OAuth 자격 증명")
    private String credential;

    @Schema(description = "OAuth 리다이렉트 URI", required = true)
    private String redirectUri;
}