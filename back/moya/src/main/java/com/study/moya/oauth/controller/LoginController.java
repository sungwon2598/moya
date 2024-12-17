package com.study.moya.oauth.controller;

import com.google.common.net.HttpHeaders;
import com.study.moya.global.config.security.SecurityHeadersConfig;
import com.study.moya.member.domain.Member;
import com.study.moya.oauth.dto.OAuthLogin.IdTokenRequestDto;
import com.study.moya.oauth.dto.OAuthLogin.MemberAuthResult;
import com.study.moya.oauth.dto.OAuthLogin.OAuthLoginResponse;
import com.study.moya.oauth.dto.token.TokenRefreshResult;
import com.study.moya.oauth.service.OauthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/oauth")
@RequiredArgsConstructor
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final OauthService oauthService;
    private final SecurityHeadersConfig securityHeadersConfig;

    @PostMapping("/login")
    public ResponseEntity<?> LoginWithGoogleOauth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) {
        log.info("authCode : {}, {}", requestBody.getAuthCode(), requestBody.getCredential());
        if (requestBody == null || requestBody.getAuthCode() == null) {
            throw new IllegalArgumentException("ID Token is required");
        }
        MemberAuthResult authResult = oauthService.loginOAuthGoogle(requestBody);

        // Access Token 쿠키 설정
        final ResponseCookie jwtTokenCookie = ResponseCookie.from("AUTH-TOKEN", authResult.getJwtToken())
                .httpOnly(true)
                .maxAge(3600) // 1시간
                .path("/")
                .secure(false)
                .build();

        // Refresh Token 쿠키 설정
        final ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH-TOKEN", authResult.getRefreshToken())
                .httpOnly(true)
                .maxAge(7 * 24 * 3600) // 7일
                .path("/")
                .secure(false)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        OAuthLoginResponse loginResponse = OAuthLoginResponse.from(authResult.getMember());
        return securityHeadersConfig.addSecurityHeaders(ResponseEntity.ok(loginResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "REFRESH-TOKEN", required = true) String refreshToken,
                                          HttpServletResponse response) {
        TokenRefreshResult refreshResult = oauthService.refreshToken(refreshToken);

        // 새로운 액세스 토큰을 쿠키에 설정
        final ResponseCookie accessTokenCookie = ResponseCookie.from("AUTH-TOKEN", refreshResult.getJwtToken())
                .httpOnly(true)
                .maxAge(3600) // 1시간
                .path("/")
                .secure(false)
                .build();

        // 새로운 리프레시 토큰을 쿠키에 설정
        final ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH-TOKEN", refreshResult.getRefreshToken())
                .httpOnly(true)
                .maxAge(7 * 24 * 3600) // 7일
                .path("/")
                .secure(false)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return securityHeadersConfig.addSecurityHeaders(ResponseEntity.ok().build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "AUTH-TOKEN", required = false) String accessToken,
            @CookieValue(name = "REFRESH-TOKEN", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null || accessToken != null) {
            oauthService.logout(accessToken, refreshToken);
        }

        // 쿠키 삭제
        ResponseCookie accessTokenCookie = ResponseCookie.from("AUTH-TOKEN", "")
                .httpOnly(true)
                .maxAge(0)
                .path("/")
                .secure(false)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH-TOKEN", "")
                .httpOnly(true)
                .maxAge(0)
                .path("/")
                .secure(false)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return securityHeadersConfig.addSecurityHeaders(ResponseEntity.ok().build());
    }

    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(member);
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