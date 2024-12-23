package com.study.moya.oauth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestControllerr {

    private static final Logger log = LoggerFactory.getLogger(TestControllerr.class);
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @GetMapping("/getRedirect")
    public String getOAuthConfig() {
        log.info("GetRedirect");
        StringBuilder config = new StringBuilder();
        config.append("OAuth2 Configuration:\n");
        config.append("Client ID: ").append(clientId).append("\n");
        config.append("Redirect URI: ").append(redirectUri);

        return config.toString();
    }
}