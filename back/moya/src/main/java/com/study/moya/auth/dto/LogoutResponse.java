package com.study.moya.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
public class LogoutResponse {
    private final String status;      // success 또는 error
    private final String message;     // 응답 메시지
    private final LocalDateTime timestamp;  // 응답 시간
    private final String detail;      // 추가적인 상세 정보 (선택적)

    // 성공 응답을 만드는 정적 팩토리 메서드
    public static LogoutResponse success(String message) {
        return LogoutResponse.builder()
                .status("success")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 에러 응답을 만드는 정적 팩토리 메서드
    public static LogoutResponse error(String message, String detail) {
        return LogoutResponse.builder()
                .status("error")
                .message(message)
                .detail(detail)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", detail='" + detail + '\'' +
                '}';
    }
}