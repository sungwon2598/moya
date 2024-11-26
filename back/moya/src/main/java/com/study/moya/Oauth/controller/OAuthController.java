package com.study.moya.Oauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.study.moya.Oauth.dto.*;
import com.study.moya.Oauth.exception.InvalidTokenException;
import com.study.moya.Oauth.service.OauthService;
import com.study.moya.auth.dto.SignupRequest;
import com.study.moya.auth.dto.UserInfoResponse;
import com.study.moya.global.config.security.SecurityHeadersConfig;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OauthService oauthService;
    private final SecurityHeadersConfig securityHeadersConfig;

    @GetMapping("/login/{provider}")
    public void socialLogin(@PathVariable String provider, HttpServletResponse response) throws IOException {
        String redirectUrl = "http://localhost:8080/oauth2/authorization/" + provider;  // 인증 엔드포인트 URI로 수정

        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/signup/complete")
    public ResponseEntity<OAuth2SignupCompleteResponse> completeSignup(
            @Valid @RequestBody OAuth2SignupCompleteRequest request) {
        log.info("OAuth 회원가입 완료 요청 - nickname: {}, token: {}", request.getNickname(), request.getToken() != null ? "exists" : "null");

        OAuth2SignupCompleteResponse response = oauthService.completeSignup(request);
        return ResponseEntity.ok(response);
    }
}




