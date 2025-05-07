package com.study.moya.oauth.dto.auth;

import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@Schema(description = "OAuth 로그인 응답")
public class OAuthLoginResponse {
    @Schema(description = "사용자 이메일")
    private String email;

    @Schema(description = "사용자 닉네임")
    private String nickname;

    @Schema(description = "사용자 권한 목록")
    private Set<Role> roles;

    @Schema(description = "사용자 상태")
    private MemberStatus status;

    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "액세스 토큰")
    private String accessToken;

    @Schema(description = "리프레쉬 토큰")
    private String refreshToken;

    public static OAuthLoginResponse from(Member member) {
        return OAuthLoginResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .roles(member.getRoles())
                .status(member.getStatus())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }

    public static OAuthLoginResponse from(Member member, String accessToken, String refreshToken) {
        return OAuthLoginResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .roles(member.getRoles())
                .status(member.getStatus())
                .profileImageUrl(member.getProfileImageUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}