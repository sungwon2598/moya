//package com.study.moya.oauth.controller;
//
//import com.google.common.net.HttpHeaders;
//import com.study.moya.global.api.ApiResponse;
//import com.study.moya.global.config.security.SecurityHeadersConfig;
//import com.study.moya.oauth.dto.token.TokenRefreshResponse;
//import com.study.moya.oauth.exception.OAuthErrorCode;
//import com.study.moya.oauth.service.TokenService;
//import com.study.moya.swagger.annotation.SwaggerErrorDescription;
//import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
//import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
//import io.swagger.v3.oas.annotations.Operation;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CookieValue;
//import org.springframework.web.bind.annotation.PostMapping;
//
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//public class TokenController {
//
//    private final TokenService tokenService;
//    private final SecurityHeadersConfig securityHeadersConfig;
//
//    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다.")
//    @SwaggerSuccessResponse(
//            status = 200,
//            name = "토큰이 성공적으로 갱신되었습니다",
//            value = TokenRefreshResponse.class
//    )
//    @SwaggerErrorDescriptions({
//            @SwaggerErrorDescription(
//                    name = "리프레시 토큰 유효하지 않음",
//                    value = OAuthErrorCode.class,
//                    code = "INVALID_REFRESH_TOKEN"
//            ),
//            @SwaggerErrorDescription(
//                    name = "회원 정보를 찾을 수 없음",
//                    value = OAuthErrorCode.class,
//                    code = "MEMBER_NOT_FOUND"
//            )
//    })
//    @PostMapping("/refresh")
//    public ResponseEntity<?> refreshToken(@CookieValue(name = "REFRESH-TOKEN", required = true) String refreshToken,
//                                          HttpServletResponse response) {
//        log.info("refresh 시작-------");
//        TokenRefreshResponse refreshResult = tokenService.refreshToken(refreshToken);
//
//        // 새로운 액세스 토큰을 쿠키에 설정
//        final ResponseCookie accessTokenCookie = ResponseCookie.from("AUTH-TOKEN", refreshResult.getJwtToken())
//                .httpOnly(true)
//                .maxAge(3600) // 1시간
//                .path("/")
//                .secure(false)
//                .build();
//
//        // 새로운 리프레시 토큰을 쿠키에 설정
//        final ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH-TOKEN", refreshResult.getRefreshToken())
//                .httpOnly(true)
//                .maxAge(7 * 24 * 3600) // 7일
//                .path("/")
//                .secure(false)
//                .build();
//
//        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
//        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
//
//        return securityHeadersConfig.addSecurityHeaders(
//                ResponseEntity.ok(ApiResponse.success()));
//    }
//}
