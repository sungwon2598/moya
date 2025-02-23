package com.study.moya.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "마이페이지 수정 요청")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPageUpdateRequest {
        @Schema(description = "닉네임", example = "모야", required = true)
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하여야 합니다.")
        private String nickname;

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        private String profileImageUrl;

        @Schema(description = "자기소개", example = "안녕하세요, 백엔드 개발자입니다.")
        @Size(max = 500, message = "자기소개는 500자 이하여야 합니다.")
        private String introduction;
}
