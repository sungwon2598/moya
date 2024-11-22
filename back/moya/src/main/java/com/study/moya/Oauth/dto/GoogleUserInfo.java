package com.study.moya.Oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoogleUserInfo {
    private String email;
    private String providerId;
    private String picture;
}
