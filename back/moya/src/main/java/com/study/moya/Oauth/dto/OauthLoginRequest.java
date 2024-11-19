package com.study.moya.Oauth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
public class OauthLoginRequest {
    @NotNull
    private String authCode;

    @NotNull
    private OAuthProvider provider;

    @Builder

    public OauthLoginRequest(String authCode, OAuthProvider provider) {
        this.authCode = authCode;
        this.provider = provider;
    }
}

