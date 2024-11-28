package com.study.moya.Oauth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class OAuthUserInfo {
    private String email;
    private String providerId;
    private String profileImageUrl;
    private String accessToken;
    private String refreshToken;
    private Instant tokenExpirationTime;
}