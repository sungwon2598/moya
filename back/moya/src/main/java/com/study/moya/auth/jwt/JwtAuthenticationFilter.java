package com.study.moya.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.auth.dto.LoginRequest;
import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ERROR_AUTH_FAILED = "인증 실패: ";
    public static final String ERROR_PARSE_FAILED = "인증 요청 파싱 실패";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            log.info("요청에 대한 인증 시도 중: {}", request.getRequestURI());
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            log.debug("이메일에 대한 로그인 요청 파싱 완료: {}", loginRequest.email());

            UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(
                    loginRequest.email(),
                    loginRequest.password()
            );

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            log.info("인증 시도 결과: {}", authentication.isAuthenticated() ? "성공" : "실패");
            return authentication;
        } catch (IOException e) {
            log.error("인증 요청 파싱 실패", e);
            throw new AuthenticationException(ERROR_PARSE_FAILED, e) {};
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        log.info("사용자 인증 성공: {}", authResult.getName());

        TokenInfo tokenInfo = jwtTokenProvider.createToken(authResult);
        log.debug("토큰 생성 완료");

        // Access Token은 헤더와 응답 본문에 포함
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + tokenInfo.getAccessToken());

        // Refresh Token은 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie("refresh_token", tokenInfo.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(604800); // 7일
        response.addCookie(refreshTokenCookie);

        response.setContentType(CONTENT_TYPE_JSON);
        response.setCharacterEncoding(CHARSET_UTF8);
        response.getWriter().write("{\"accessToken\":\"" + tokenInfo.getAccessToken() + "\"}");

        log.info("사용자에 대한 인증 응답 전송 완료: {}", authResult.getName());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        log.warn("인증 실패: {}", failed.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(CONTENT_TYPE_JSON);
        response.setCharacterEncoding(CHARSET_UTF8);
        response.getWriter().write("{\"error\":\"" + ERROR_AUTH_FAILED + failed.getMessage() + "\"}");
        log.info("인증 실패 응답 전송 완료");
    }
}