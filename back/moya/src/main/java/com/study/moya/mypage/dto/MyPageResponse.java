package com.study.moya.mypage.dto;

import com.study.moya.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "마이페이지 응답")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPageResponse {
    @Schema(description = "닉네임", example = "모야")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "자기소개", example = "안녕하세요, 백엔드 개발자입니다.")
    private String introduction;

    public static MyPageResponse from(Member member) {
        MyPageResponse response = new MyPageResponse();
        response.nickname = member.getNickname();
        response.profileImageUrl = member.getProfileImageUrl();
        response.introduction = member.getIntroduction();
        return response;
    }
}
