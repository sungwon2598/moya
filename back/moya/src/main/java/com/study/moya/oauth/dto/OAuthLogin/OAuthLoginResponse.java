package com.study.moya.oauth.dto.OAuthLogin;

import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.domain.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class OAuthLoginResponse {
    private String email;
    private String nickname;
    private Set<Role> roles;
    private MemberStatus status;
    private String profileImageUrl;

    public static OAuthLoginResponse from(Member member) {
        return OAuthLoginResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .roles(member.getRoles())
                .status(member.getStatus())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}