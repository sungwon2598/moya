package com.study.moya.Oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class OauthLoginRequest {
    private String authCode;
    private OAuthProvider provider;
}
