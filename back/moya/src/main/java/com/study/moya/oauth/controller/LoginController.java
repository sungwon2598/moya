package com.study.moya.oauth.controller;

import com.study.moya.global.api.ApiResponse;
import com.study.moya.global.config.security.SecurityHeadersConfig;
import com.study.moya.oauth.exception.OAuthErrorCode;
import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/oauth")
@RequiredArgsConstructor
@Tag(name = "OAuth", description = "OAuth 인증 관련 API")
public class LoginController {
    private final SecurityHeadersConfig securityHeadersConfig;

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
            @AuthenticationPrincipal String memberId,
            @CookieValue(name = "REFRESH-TOKEN", required = false) String refreshToken,
            HttpServletResponse response) {

//        log.info("logout요청 받음==========================");
//        if (refreshToken != null) {
//            memberOAuthService.logout(memberId, refreshToken);
//        }
        return securityHeadersConfig.addSecurityHeaders(
                ResponseEntity.ok(ApiResponse.success()));
    }
}