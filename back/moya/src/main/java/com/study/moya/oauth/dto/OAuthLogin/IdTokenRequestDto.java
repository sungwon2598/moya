package com.study.moya.oauth.dto.OAuthLogin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdTokenRequestDto {
    private String authCode;
    private String credential;
}