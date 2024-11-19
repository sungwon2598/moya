package com.study.moya.Oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OauthLoginResponse {
    private String accessToken;
    private String refreshToken;
    private boolean isNewUser;
    private String nextStep;

    @Builder
    public OauthLoginResponse(String accessToken, String refreshToken, boolean isNewUser, String nextStep) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isNewUser = isNewUser;
        this.nextStep = nextStep;
    }
}
