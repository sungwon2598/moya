package com.study.moya.auth.jwt;

import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    private final JwtTokenProvider jwtTokenProvider;
    
    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;
    
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // /refresh 엔드포인트는 필터에서 처리하지 않음
        if (request.getRequestURI().equals("/api/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("JwtAuthorizationFilter를 통해 요청 처리 중: {}", request.getRequestURI());

        String accessToken = resolveAccessToken(request);
        log.debug("추출된 액세스 토큰: {}", accessToken != null ? "존재함" : "존재하지 않음");

        if (StringUtils.hasText(accessToken)) {
            if (jwtTokenProvider.validateToken(accessToken)) {
                Authentication auth = jwtTokenProvider.getAuthentication(accessToken);
                if (auth != null && auth.getPrincipal() != null) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("'{}' 사용자에 대한 인증 정보를 보안 컨텍스트에 설정했습니다. URI: {}",
                            auth.getName(), request.getRequestURI());
                } else {
                    log.warn("인증 정보 설정에 실패했습니다. 인증 객체 또는 주체가 null입니다.");
                }
            } else {
                log.debug("액세스 토큰이 만료되었습니다. 리프레시 토큰 확인 중...");
                // access token이 만료된 경우 refresh token으로 새 토큰 발급 시도
                String refreshToken = resolveRefreshToken(request);
                if (StringUtils.hasText(refreshToken)) {
                    try {
                        TokenInfo newTokens = jwtTokenProvider.refreshAccessToken(refreshToken);
                        // 새로운 토큰으로 인증 처리
                        Authentication auth = jwtTokenProvider.getAuthentication(newTokens.getAccessToken());
                        if (auth != null && auth.getPrincipal() != null) {
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            log.info("토큰 갱신 성공. '{}' 사용자 인증 완료. URI: {}",
                                    auth.getName(), request.getRequestURI());
                            
                            // 새로운 토큰을 쿠키에 설정
                            setTokenCookies(response, newTokens);
                        }
                    } catch (Exception e) {
                        log.warn("토큰 갱신 실패: {}", e.getMessage());
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        
                        // Refresh Token 만료 시 명확한 에러 헤더 설정
                        if (e.getMessage().contains("만료된") || e.getMessage().contains("유효하지 않은")) {
                            response.setHeader("X-Auth-Error", "REFRESH_TOKEN_EXPIRED");
                            log.info("Refresh Token 만료 - 재로그인 필요: {}", request.getRequestURI());
                            
                            // 만료된 쿠키 삭제
                            clearTokenCookies(response);
                        }
                    }
                } else {
                    log.debug("리프레시 토큰이 없습니다. 인증 실패.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        // Authorization 헤더에서 토큰 확인
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        // 쿠키에서 access token 확인
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    
    private void setTokenCookies(HttpServletResponse response, TokenInfo tokenInfo) {
        // OAuth 핸들러와 동일한 ResponseCookie 방식으로 통일
        ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, tokenInfo.getAccessToken())
                .domain(".moyastudy.com")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(accessTokenExpiration / 1000)
                .sameSite("Lax")
                .build();
        
        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, tokenInfo.getRefreshToken())
                .domain(".moyastudy.com")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration / 1000)
                .sameSite("Lax")
                .build();
        
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        
        log.debug("새로운 토큰을 쿠키에 설정했습니다. (ResponseCookie 방식)");
    }
    
    private void clearTokenCookies(HttpServletResponse response) {
        // 만료된 토큰 쿠키 삭제
        ResponseCookie expiredAccessCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
                .domain(".moyastudy.com")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        
        ResponseCookie expiredRefreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .domain(".moyastudy.com")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        
        response.addHeader("Set-Cookie", expiredAccessCookie.toString());
        response.addHeader("Set-Cookie", expiredRefreshCookie.toString());
        
        log.debug("만료된 토큰 쿠키를 삭제했습니다.");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.info("지금 들어온 요청은 스킵됩니다. URI: {}", request.getRequestURI());
        String[] excludePath = {"/swagger-ui/index.html",
                "/swagger-ui/swagger-ui-standalone-preset.js",
                "/swagger-ui/swagger-initializer.js",
                "/swagger-ui/swagger-ui-bundle.js",
                "/swagger-ui/swagger-ui.css",
                "/swagger-ui/index.css",
                "/favicon.ico",
                "/swagger-ui/favicon-32x32.png",
                "/swagger-ui/favicon-16x16.png",
                "/api-docs/json/swagger-config",
                "/api-docs/json"};
        String path = request.getRequestURI();
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }


}