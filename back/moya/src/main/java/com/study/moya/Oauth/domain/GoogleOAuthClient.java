package com.study.moya.Oauth.domain;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.study.moya.Oauth.dto.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {
    private final WebClient webClient;

    public GoogleUserInfo getUserInfo(String authCode){
        GoogleTokenResponse tokenResponse = webClient.post()
                .uri(properties.getTokenUri())
                .body(BodyInserters.fromFormData("code", authCode))
                .with("client_id", properties.getClienId())
    }

}
