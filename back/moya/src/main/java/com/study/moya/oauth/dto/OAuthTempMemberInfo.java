package com.study.moya.oauth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class OAuthTempMemberInfo {
    private String email;
    private String providerId;
    private String profileImageUrl;
    private String accessToken;
    private String refreshToken;
    private Instant tokenExpirationTime;

    // 정적 팩토리 메서드
    public static OAuthTempMemberInfo from(OAuthUserInfo userInfo) {
        return OAuthTempMemberInfo.builder()
                .email(userInfo.getEmail())
                .providerId(userInfo.getProviderId())
                .profileImageUrl(userInfo.getProfileImageUrl())
                .accessToken(userInfo.getAccessToken())
                .refreshToken(userInfo.getRefreshToken())
                .tokenExpirationTime(userInfo.getTokenExpirationTime())
                .build();
    }

}
