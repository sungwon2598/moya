package com.study.moya.mypage.dto;

import com.study.moya.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마이페이지 응답")
public record MyPageResponse(
        @Schema(description = "닉네임", example = "모야")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String profileImageUrl,

        @Schema(description = "자기소개", example = "안녕하세요, 백엔드 개발자입니다.")
        String introduction
) {
    public static MyPageResponse from(Member member) {
        return new MyPageResponse(
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getIntroduction()
        );
    }
}
