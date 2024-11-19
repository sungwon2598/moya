package com.study.moya.Oauth.controller;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.study.moya.Oauth.dto.OauthLoginRequest;
import com.study.moya.Oauth.dto.OauthLoginResponse;
import com.study.moya.Oauth.service.OauthService;
import com.study.moya.auth.dto.UserInfoResponse;
import com.study.moya.global.config.security.SecurityHeadersConfig;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OauthService oauthService;
    private final SecurityHeadersConfig securityHeadersConfig;

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getUserInfo(Authentication authentication) {
        UserInfoResponse userInfoResponse = oauthService.getUserInfo(authentication);
        return securityHeadersConfig.addSecurityHeaders(ResponseEntity
                .ok()
                .body(userInfoResponse));
    }

    @GetMapping("/login/{provider}")
    public void socialLogin(@PathVariable String provider, HttpServletResponse response) throws IOException {
        String redirectUrl = "http://moya.com/oauth2/authorization/" + provider;  // 인증 엔드포인트 URI로 수정
        response.sendRedirect(redirectUrl);
    }


}


