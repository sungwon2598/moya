package com.study.moya.oauth.dto.OAuthLogin;

import com.study.moya.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberAuthResult {
    private String jwtToken;
    private String refreshToken;
    private Member member;
}
