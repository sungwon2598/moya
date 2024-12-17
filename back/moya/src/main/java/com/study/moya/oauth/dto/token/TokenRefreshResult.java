package com.study.moya.oauth.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenRefreshResult {
    private String jwtToken;
    private String refreshToken;
}