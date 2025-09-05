package com.study.moya.oauth.utils;

import com.study.moya.auth.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CookieUtils {

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;
    
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    public void setProductionCookies(HttpServletResponse response, JwtTokenProvider.TokenInfo tokenInfo) {
        log.info("운영용 쿠키 설정 (.moyastudy.com 도메인)");
        
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", tokenInfo.getAccessToken())
                .domain(".moyastudy.com") // moyastudy.com 도메인에서만 사용
                .httpOnly(true)
                .secure(true) // HTTPS 필수
                .path("/")
                .maxAge(accessTokenExpiration / 1000)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", tokenInfo.getRefreshToken())
                .domain(".moyastudy.com")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration / 1000)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}