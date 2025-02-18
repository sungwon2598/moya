package com.study.moya.oauth.controller;

import com.google.common.net.HttpHeaders;
import com.study.moya.global.api.ApiResponse;
import com.study.moya.global.config.security.SecurityHeadersConfig;
import com.study.moya.member.domain.Member;
import com.study.moya.oauth.dto.auth.OAuthLoginRequest;
import com.study.moya.oauth.dto.member.MemberAuthResponse;
import com.study.moya.oauth.dto.auth.OAuthLoginResponse;
import com.study.moya.oauth.dto.token.TokenRefreshResponse;
import com.study.moya.oauth.exception.OAuthErrorCode;
import com.study.moya.oauth.service.MemberOAuthService;
import com.study.moya.oauth.service.OAuthFacadeService;
import com.study.moya.oauth.service.TokenService;
import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/oauth")
@RequiredArgsConstructor
@Tag(name = "OAuth", description = "OAuth 인증 관련 API")
public class LoginController {

    private final OAuthFacadeService oauthService;
    private final SecurityHeadersConfig securityHeadersConfig;
    private final MemberOAuthService memberOAuthService;

    @Operation(summary = "OAuth 로그인", description = "Google OAuth를 통한 로그인을 처리합니다.")
    @SwaggerSuccessResponse(
            status = 200,
            name = "로그인이 완료됐습니다",
            value = OAuthLoginResponse.class
    )
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(
                    name = "인증 코드 유효하지 않음",
                    value = OAuthErrorCode.class,
                    code = "INVALID_AUTH_CODE"
            ),
            @SwaggerErrorDescription(
                    name = "Google API 오류",
                    value = OAuthErrorCode.class,
                    code = "GOOGLE_API_ERROR"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<OAuthLoginResponse>> LoginWithGoogleOauth2(@RequestBody OAuthLoginRequest requestBody,
                                                                                 HttpServletResponse response) {
        log.info("authCode : {}, credential: {}, redirectUrl : {}",
                requestBody.getAuthCode(),
                requestBody.getCredential(),
                requestBody.getRedirectUri());
        if (requestBody == null || requestBody.getAuthCode() == null) {
            throw new IllegalArgumentException("ID Token is required");
        }
        MemberAuthResponse authResult = oauthService.loginOAuthGoogle(requestBody);

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
        return securityHeadersConfig.addSecurityHeaders(
                ResponseEntity.ok(ApiResponse.of(loginResponse)));
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리하고 토큰을 무효화합니다.")
    @SwaggerSuccessResponse(
            name = "로그아웃이 완료됐습니다"
    )
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(
                    name = "로그아웃 처리 실패",
                    value = OAuthErrorCode.class,
                    code = "LOGOUT_FAILED"
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "AUTH-TOKEN", required = false) String accessToken,
            @CookieValue(name = "REFRESH-TOKEN", required = false) String refreshToken,
            HttpServletResponse response) {

        log.info("logout요청 받음==========================");
        if (refreshToken != null || accessToken != null) {
            log.info("ac, rf : {}, {}", accessToken, refreshToken);
            memberOAuthService.logout(accessToken, refreshToken);
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

        return securityHeadersConfig.addSecurityHeaders(
                ResponseEntity.ok(ApiResponse.success()));
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 처리하고 관련된 모든 정보를 삭제합니다.")
    @SwaggerSuccessResponse(
            name = "회원 탈퇴가 완료됐습니다"
    )
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(
                    name = "회원 정보를 찾을 수 없음",
                    value = OAuthErrorCode.class,
                    code = "MEMBER_NOT_FOUND"
            ),
            @SwaggerErrorDescription(
                    name = "회원 탈퇴 처리 실패",
                    value = OAuthErrorCode.class,
                    code = "WITHDRAWAL_FAILED"
            )
    })
    @PostMapping("/withdraw")
    @Transactional  // 트랜잭션 추가 필요
    public ResponseEntity<?> withdraw(
            @CookieValue(name = "AUTH-TOKEN", required = false) String accessToken,
            @CookieValue(name = "REFRESH-TOKEN", required = false) String refreshToken,  // 리프레시 토큰도 함께 처리
            HttpServletResponse response
    ) {
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 토큰이 필요합니다.");
        }

        try {
            memberOAuthService.withdraw(accessToken);

            // 쿠키 삭제
            ResponseCookie accessTokenCookie = ResponseCookie.from("AUTH-TOKEN", "")
                    .httpOnly(true)
                    .maxAge(0)
                    .path("/")
                    .secure(true)  // HTTPS 사용 시 secure 활성화
                    .sameSite("Strict")  // CSRF 방지
                    .build();

            ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH-TOKEN", "")
                    .httpOnly(true)
                    .maxAge(0)
                    .path("/")
                    .secure(true)
                    .sameSite("Strict")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return securityHeadersConfig.addSecurityHeaders(ResponseEntity.ok().build());
        } catch (Exception e) {
            log.error("회원 탈퇴 처리 중 오류 발생: {}", e.getMessage(), e);
            return securityHeadersConfig.addSecurityHeaders(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("회원 탈퇴 처리 중 오류가 발생했습니다.")
            );
        }
    }
}