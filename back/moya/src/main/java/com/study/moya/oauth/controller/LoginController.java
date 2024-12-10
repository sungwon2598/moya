package com.study.moya.oauth.controller;

import com.google.common.net.HttpHeaders;
import com.study.moya.oauth.dto.IdTokenRequestDto;
import com.study.moya.oauth.dto.OAuthLogin.MemberAuthResult;
import com.study.moya.oauth.dto.OAuthLogin.OAuthLoginResponse;
import com.study.moya.oauth.service.OauthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/oauth")
@RequiredArgsConstructor
public class LoginController {

    private final OauthService oauthService;

    @PostMapping("/login")
    public ResponseEntity<?> LoginWithGoogleOauth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) {
        if (requestBody == null || requestBody.getCredential() == null) {
            throw new IllegalArgumentException("ID Token is required");
        }
        MemberAuthResult authResult = oauthService.loginOAuthGoogle(requestBody);

        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", authResult.getToken())
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(false)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        OAuthLoginResponse loginResponse = OAuthLoginResponse.from(authResult.getMember());
        return ResponseEntity.ok(loginResponse);
    }



//    @PostMapping("/login")
//    public ResponseEntity<?> LoginWithGoogleOauth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) {
//        if (requestBody == null || requestBody.getCredential() == null) {
//            throw new IllegalArgumentException("ID Token is required");
//        }
//        String authToken = oauthService.loginOAuthGoogle(requestBody, requestBody.getRedirectUrl());
//        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", authToken)
//                .httpOnly(true)
//                .maxAge(7 * 24 * 3600)
//                .path("/")
//                .secure(false)
//                .build();
//        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//        return ResponseEntity.ok().build();
//    }
}