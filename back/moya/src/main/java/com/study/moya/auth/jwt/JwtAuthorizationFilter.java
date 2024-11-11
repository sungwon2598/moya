package com.study.moya.auth.jwt;

import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("JwtAuthorizationFilter를 통해 요청 처리 중: {}", request.getRequestURI());

        // 액세스 토큰 처리
        String accessToken = resolveAccessToken(request);
        log.debug("추출된 액세스 토큰: {}", accessToken != null ? "존재함" : "존재하지 않음");

        if (StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken)) {
            Authentication auth = jwtTokenProvider.getAuthentication(accessToken);
            if (auth != null && auth.getPrincipal() != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("'{}' 사용자에 대한 인증 정보를 보안 컨텍스트에 설정했습니다. URI: {}",
                        auth.getName(), request.getRequestURI());
            } else {
                log.warn("인증 정보 설정에 실패했습니다. 인증 객체 또는 주체가 null입니다.");
            }
        } else {
            // 액세스 토큰이 유효하지 않은 경우, 리프레시 토큰 확인
            String refreshToken = resolveRefreshToken(request);
            if (StringUtils.hasText(refreshToken) && shouldAttemptRefresh(request)) {
                try {
                    TokenInfo newTokens = jwtTokenProvider.refreshAccessToken(refreshToken);
                    response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newTokens.getAccessToken());

                    // 새로운 리프레시 토큰을 쿠키에 설정
                    addRefreshTokenCookie(response, newTokens.getRefreshToken());

                    // 새로운 액세스 토큰으로 인증 설정
                    Authentication auth = jwtTokenProvider.getAuthentication(newTokens.getAccessToken());
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    log.info("토큰이 성공적으로 갱신되었습니다. 사용자: {}", auth.getName());
                } catch (Exception e) {
                    log.warn("토큰 갱신 실패: {}", e.getMessage());
                }
            } else {
                log.debug("유효한 JWT 토큰을 찾을 수 없습니다. URI: {}", request.getRequestURI());
            }
        }

        filterChain.doFilter(request, response);
        log.debug("JwtAuthorizationFilter를 통한 요청 처리 완료: {}", request.getRequestURI());
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            log.debug("Authorization 헤더에서 액세스 토큰을 찾았습니다");
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        log.debug("Authorization 헤더에서 액세스 토큰을 찾을 수 없습니다");
        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    log.debug("쿠키에서 리프레시 토큰을 찾았습니다");
                    return cookie.getValue();
                }
            }
        }
        log.debug("쿠키에서 리프레시 토큰을 찾을 수 없습니다");
        return null;
    }

    private boolean shouldAttemptRefresh(HttpServletRequest request) {
        // 토큰 갱신을 시도하지 않을 경로들
        String path = request.getRequestURI();
        return !path.equals("/api/auth/refresh") &&
                !path.equals("/api/auth/login") &&
                !path.equals("/api/auth/logout") &&
                !path.equals("/api/auth/signup");
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS 환경에서만 사용
        cookie.setPath("/");
        cookie.setMaxAge(604800); // 7일
        response.addCookie(cookie);
    }
}