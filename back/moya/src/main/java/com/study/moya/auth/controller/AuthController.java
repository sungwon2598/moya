package com.study.moya.auth.controller;

import com.study.moya.oauth.exception.InvalidTokenException;
import com.study.moya.auth.dto.*;
import com.study.moya.auth.exception.InvalidRefreshTokenException;
import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import com.study.moya.auth.service.AuthService;
import com.study.moya.global.config.security.SecurityHeadersConfig;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "사용자 인증 및 등록을 위한 API")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private final AuthService authService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final SecurityHeadersConfig securityHeadersConfig;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        memberService.registerNewUser(signUpRequest);
        return securityHeadersConfig.addSecurityHeaders(ResponseEntity
                .ok()
                .body("회원가입이 성공적으로 이루어졌습니다"));
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getUserInfo(Authentication authentication) {
        UserInfoResponse userInfoResponse = authService.getUserInfo(authentication);
        return securityHeadersConfig.addSecurityHeaders(ResponseEntity
                .ok()
                .body(userInfoResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletResponse response) {
        log.info("로그인 요청 시작 - 이메일: {}", loginRequest.email());
        try {
            TokenInfo tokenInfo = authService.authenticateUser(loginRequest);
            log.info("토큰 생성 완료");

            Map<String, String> tokenResponse = new HashMap<>();
            tokenResponse.put("accessToken", tokenInfo.getAccessToken());

            addRefreshTokenCookie(response, tokenInfo.getRefreshToken());
            log.info("토큰 쿠키 설정 완료");

            return securityHeadersConfig.addSecurityHeaders(ResponseEntity
                    .ok()
                    .body(tokenResponse));
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 쿠키 디버깅을 위한 로깅 추가
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            log.debug("요청에 포함된 쿠키 목록:");
            for (Cookie cookie : cookies) {
                log.debug("쿠키 이름: {}, 값: {}, 도메인: {}, 경로: {}",
                        cookie.getName(),
                        cookie.getValue(),
                        cookie.getDomain(),
                        cookie.getPath());
            }
        } else {
            log.debug("요청에 쿠키가 없습니다.");
        }

        // refreshToken 파라미터 확인
        log.debug("@CookieValue로 받은 리프레시 토큰: {}",
                refreshToken != null ? "존재함(" + refreshToken.substring(0, 10) + "...)" : "null");

        if (refreshToken == null) {
            return securityHeadersConfig.addSecurityHeaders(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("리프레시 토큰이 없습니다."));
        }

        try {
            TokenInfo tokenInfo = authService.refreshToken(refreshToken);
            log.debug("토큰 갱신 성공");

            Map<String, String> tokenResponse = new HashMap<>();
            tokenResponse.put("accessToken", tokenInfo.getAccessToken());

            addRefreshTokenCookie(response, tokenInfo.getRefreshToken());
            log.debug("새로운 리프레시 토큰을 쿠키에 설정");

            return securityHeadersConfig.addSecurityHeaders(ResponseEntity
                    .ok()
                    .body(tokenResponse));
        } catch (InvalidRefreshTokenException e) {
            log.error("리프레시 토큰 갱신 실패: {}", e.getMessage());
            deleteRefreshTokenCookie(response);
            return securityHeadersConfig.addSecurityHeaders(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            @RequestParam String email,
            HttpServletResponse response) {

        log.info("로그아웃 요청 받음 - email: {}", email);

        try{
            if(accessToken == null){
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(LogoutResponse.error("Authorization 헤더가 필요합니다", null));
            }

            String token = accessToken.replace("Bearer ", "");
            LogoutRequest request = LogoutRequest.builder()
                    .email(email)
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .build();

            log.debug("Logout request created: {}", request);
            authService.logout(request);
            log.info("Access Token + Refresh Token 무효화 완료");

            deleteRefreshTokenCookie(response);
            log.info("Refresh Token 쿠키 제거 완료");

            SecurityContextHolder.clearContext();
            log.info("시큐리티 컨텍스트 클리어 완료");

            return ResponseEntity.ok(LogoutResponse.success("로그아웃되었습니다"));
        } catch (InvalidTokenException e) {
            log.error("Invalid token error during logout: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(LogoutResponse.error("인증 오류", e.getMessage()));
        } catch (Exception e){
            log.error("Unexpected error during logout", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LogoutResponse.error(
                            "로그아웃 처리 중 오류가 발생했습니다",
                            e.getMessage()
                    ));
        }

    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(604800); // 7일
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
        log.debug("리프레시 토큰 쿠키 설정 완료 - 경로: {}, Secure: {}, HttpOnly: {}",
                cookie.getPath(),
                cookie.getSecure(),
                cookie.isHttpOnly());
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

}