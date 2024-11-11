package com.study.moya.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청 데이터")
public record SignupRequest(
        @Schema(description = "이메일 주소", example = "user@example.com")
        @NotBlank(message = ErrorMessages.EMAIL_REQUIRED)
        @Email(message = ErrorMessages.EMAIL_INVALID)
        String email,

        @Schema(description = "비밀번호", example = "strongPassword123!",
                minLength = Password.MIN_LENGTH, maxLength = Password.MAX_LENGTH)
        @NotBlank(message = ErrorMessages.PASSWORD_REQUIRED)
        @Size(min = Password.MIN_LENGTH, max = Password.MAX_LENGTH, message = Password.MESSAGE)
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
                message = ErrorMessages.PASSWORD_PATTERN)
        String password,

        @Schema(description = "닉네임", example = "닉네임123",
                minLength = Nickname.MIN_LENGTH, maxLength = Nickname.MAX_LENGTH)
        @NotBlank(message = ErrorMessages.NICKNAME_REQUIRED)
        @Size(min = Nickname.MIN_LENGTH, max = Nickname.MAX_LENGTH, message = Nickname.MESSAGE)
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String profileImageUrl,

        @Schema(description = "제공자 ID", example = "kakao_12345")
        String providerId,

        @Schema(description = "이용약관 동의 여부", required = true)
        @NotNull(message = ErrorMessages.TERMS_REQUIRED)
        Boolean termsAgreed,

        @Schema(description = "개인정보 처리방침 동의 여부", required = true)
        @NotNull(message = ErrorMessages.PRIVACY_POLICY_REQUIRED)
        Boolean privacyPolicyAgreed,

        @Schema(description = "마케팅 수신 동의 여부")
        Boolean marketingAgreed
) {
    public static final class Nickname {
        public static final int MIN_LENGTH = 2;
        public static final int MAX_LENGTH = 30;
        public static final String MESSAGE =
                "닉네임은 반드시 " + MIN_LENGTH + "자 이상 " + MAX_LENGTH + "자 이하여야 합니다";
    }

    public static final class Password {
        public static final int MIN_LENGTH = 8;
        public static final int MAX_LENGTH = 40;
        public static final String MESSAGE =
                "비밀번호는 반드시 " + MIN_LENGTH + "자 이상 " + MAX_LENGTH + "자 이하여야 합니다";
    }

    public static final class ErrorMessages {
        public static final String EMAIL_REQUIRED = "이메일은 필수입니다";
        public static final String EMAIL_INVALID = "올바른 이메일 형식이어야 합니다";
        public static final String PASSWORD_REQUIRED = "비밀번호는 필수입니다";
        public static final String PASSWORD_PATTERN =
                "비밀번호는 숫자, 소문자, 대문자, 특수문자를 각각 하나 이상 포함해야 합니다";
        public static final String NICKNAME_REQUIRED = "닉네임은 필수입니다";
        public static final String TERMS_REQUIRED = "이용약관 동의는 필수입니다";
        public static final String PRIVACY_POLICY_REQUIRED = "개인정보 처리방침 동의는 필수입니다";
    }
}