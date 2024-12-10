package com.study.moya.oauth.dto.OAuthLogin;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthAccessToken {
    private String oAuthAccessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;

    @Builder
    public OAuthAccessToken(String oAuthAccessToken, String refreshToken, String tokenType, Long expiresIn) {
        this.oAuthAccessToken = oAuthAccessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }
}