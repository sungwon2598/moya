package com.study.moya.oauth.dto.OAuthLogin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleAccessToken {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("id_token")
    private String idToken;

    public OAuthAccessToken toEntity() {
        return OAuthAccessToken.builder()
                .oAuthAccessToken(this.accessToken)
                .refreshToken(this.refreshToken)
                .tokenType(this.tokenType)
                .expiresIn(this.expiresIn)
                .build();
    }
}