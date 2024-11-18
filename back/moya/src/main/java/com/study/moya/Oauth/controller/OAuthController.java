package com.study.moya.Oauth.controller;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.study.moya.Oauth.dto.OauthLoginRequest;
import com.study.moya.Oauth.dto.OauthLoginResponse;
import com.study.moya.Oauth.service.OauthService;
import com.study.moya.auth.dto.UserInfoResponse;
import com.study.moya.global.config.security.SecurityHeadersConfig;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}

@PostMapping("/login")
public ResponseEntity<?> oauthLogin(@RequestBody OauthLoginRequest request){
    try{
        OauthLoginResponse response = oauthService.

}
