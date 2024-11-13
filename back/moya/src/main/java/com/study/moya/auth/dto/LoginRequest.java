package com.study.moya.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 데이터")
public record LoginRequest(
        @Schema(description = "이메일 주소", example = "user@example.com")
        @NotBlank(message = ErrorMessages.EMAIL_REQUIRED)
        @Email(message = ErrorMessages.EMAIL_INVALID)
        String email,

        @Schema(description = "비밀번호", example = "strongPassword123!")
        @NotBlank(message = ErrorMessages.PASSWORD_REQUIRED)
        String password
)

{

    public static final class ErrorMessages {
        public static final String EMAIL_REQUIRED = "이메일은 필수입니다";
        public static final String EMAIL_INVALID = "올바른 이메일 형식이어야 합니다";
        public static final String PASSWORD_REQUIRED = "비밀번호는 필수입니다";
    }
}