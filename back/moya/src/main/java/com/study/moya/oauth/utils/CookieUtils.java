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

    public boolean isLocalRequest(String origin, String referer) {
        // Origin이나 Referer에 localhost가 포함된 경우
        if ((origin != null && origin.contains("localhost")) ||
            (referer != null && referer.contains("localhost"))) {
            return true;
        }
        
        // Origin과 Referer가 모두 null인 경우 (Swagger, Postman 등 직접 API 호출)
        // 운영 환경에서는 일반적으로 Origin이나 Referer가 있으므로, 둘 다 null이면 로컬 환경으로 판단
        if (origin == null && referer == null) {
            return true;
        }
        
        return false;
    }

    public void setLocalCookies(HttpServletResponse response, JwtTokenProvider.TokenInfo tokenInfo) {
        log.info("로컬용 쿠키 설정 (도메인 없음, HTTP 허용, 3시간)");
        
        // 로컬 테스트용으로 3시간(10800초) 설정
        long localTestDuration = 3 * 60 * 60;
        
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", tokenInfo.getAccessToken())
                .httpOnly(true)
                .secure(false) // HTTP도 허용
                .path("/")
                .maxAge(localTestDuration)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", tokenInfo.getRefreshToken())
                .httpOnly(true)
                .secure(false) // HTTP도 허용
                .path("/")
                .maxAge(localTestDuration)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }

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

    public String getRedirectUrl(String origin, String referer) {
        if (isLocalRequest(origin, referer)) {
            return "http://localhost:3000/";
        } else {
            return "https://moyastudy.com/";
        }
    }

    public String getErrorRedirectUrl(String origin, String referer) {
        if (isLocalRequest(origin, referer)) {
            return "http://localhost:3000/?auth=error";
        } else {
            return "https://moyastudy.com/?auth=error";
        }
    }
}