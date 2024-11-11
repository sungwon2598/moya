package com.study.moya.auth.controller;

import com.study.moya.auth.dto.LoginRequest;
import com.study.moya.auth.dto.SignupRequest;
import com.study.moya.auth.dto.UserInfoResponse;
import com.study.moya.auth.exception.InvalidRefreshTokenException;
import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import com.study.moya.auth.service.AuthService;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "사용자 인증 및 등록을 위한 API")
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        memberService.registerNewUser(signUpRequest);
        return ResponseEntity.ok("회원가입이 성공적으로 이루어졌습니다");
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getUserInfo(Authentication authentication) {
        UserInfoResponse userInfoResponse = authService.getUserInfo(authentication);
        return ResponseEntity.ok(userInfoResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletResponse response) {
        log.info("로그인 요청 시작 - 이메일: {}", loginRequest.email());
        try {
            TokenInfo tokenInfo = authService.authenticateUser(loginRequest);
            log.info("토큰 생성 완료");

            // Access Token은 응답 본문에 포함
            Map<String, String> tokenResponse = new HashMap<>();
            tokenResponse.put("accessToken", tokenInfo.getAccessToken());

            // Refresh Token은 쿠키에 설정
            addRefreshTokenCookie(response, tokenInfo.getRefreshToken());
            log.info("토큰 쿠키 설정 완료");

            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken,
                                          HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("리프레시 토큰이 없습니다.");
        }

        try {
            TokenInfo tokenInfo = authService.refreshToken(refreshToken);

            // 새로운 Access Token을 응답 본문에 포함
            Map<String, String> tokenResponse = new HashMap<>();
            tokenResponse.put("accessToken", tokenInfo.getAccessToken());

            // 새로운 Refresh Token을 쿠키에 설정
            addRefreshTokenCookie(response, tokenInfo.getRefreshToken());

            return ResponseEntity.ok(tokenResponse);
        } catch (InvalidRefreshTokenException e) {
            // Refresh Token이 유효하지 않은 경우 쿠키 삭제
            deleteRefreshTokenCookie(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        //deleteRefreshTokenCookie(response);
        log.info("로그아웃 성공");
        return ResponseEntity.ok().body("로그아웃되었습니다");
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true); // HTTPS 환경에서만 사용
        cookie.setMaxAge(604800); // 7일
        response.addCookie(cookie);
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
