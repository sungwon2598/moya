package com.study.moya.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class LogoutRequest {
    private final String email;
    private final String accessToken;
    private final String refreshToken;

    // toString 메서드 추가 (디버깅용)
    @Override
    public String toString() {
        return "LogoutRequest{" +
                "email='" + email + '\'' +
                ", accessToken='" + (accessToken != null ? "[PROTECTED]" : "null") + '\'' +
                ", refreshToken='" + (refreshToken != null ? "[PROTECTED]" : "null") + '\'' +
                '}';
    }
}
