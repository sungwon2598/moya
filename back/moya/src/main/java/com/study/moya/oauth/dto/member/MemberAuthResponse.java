package com.study.moya.oauth.dto.member;

import com.study.moya.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "회원 인증 결과")
public class MemberAuthResponse {
    @Schema(description = "JWT 토큰")
    private String jwtToken;

    @Schema(description = "리프레시 토큰")
    private String refreshToken;

    @Schema(description = "회원 정보")
    private Member member;
}
