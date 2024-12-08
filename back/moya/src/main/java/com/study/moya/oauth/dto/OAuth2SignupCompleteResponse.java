package com.study.moya.oauth.dto;

import com.study.moya.member.domain.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class OAuth2SignupCompleteResponse {
    private final String message;            // 응답 메시지
    private final String status;             // 상태
    private final String email;              // 이메일
    private final String nickname;           // 닉네임
    private final Set<Role> roles;           // 권한 목록
    private final String accessToken;        // JWT 액세스 토큰
}