package com.study.moya.Oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2SignupCompleteRequest {
    private String token;
    private String nickname;
    private boolean termsAgreed;
    private boolean privacyPolicyAgreed;
    private boolean marketingAgreed;
}
