package com.study.moya.global.config.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SecurityHeadersConfig {

    public <T> ResponseEntity<T> addSecurityHeaders(ResponseEntity<T> responseEntity) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-store");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("X-XSS-Protection", "1; mode=block");

        return ResponseEntity
                .status(responseEntity.getStatusCode())
                .headers(headers)
                .body(responseEntity.getBody());
    }
}
