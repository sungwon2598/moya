package com.study.moya.oauth.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthAccessToken {

    private String oAuthAccessToken;
    private String oAuthRefreshToken;
    private String oAuthTokenType;

    @Builder
    private OAuthAccessToken(String oAuthAccessToken, String oAuthRefreshToken, String oAuthTokenType) {
        this.oAuthAccessToken = oAuthAccessToken;
        this.oAuthRefreshToken = oAuthRefreshToken;
        this.oAuthTokenType = oAuthTokenType;
    }

    public static OAuthAccessToken of(String oAuthAccessToken, String oAuthRefreshToken, String oAuthTokenType) {
        return OAuthAccessToken.builder()
                .oAuthAccessToken(oAuthAccessToken)
                .oAuthRefreshToken(oAuthRefreshToken)
                .oAuthTokenType(oAuthTokenType)
                .build();
    }

    public String authorizationHeaderValue() {
        return oAuthTokenType + " " + oAuthAccessToken;
    }
}