package com.study.moya.oauth.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "Google 사용자 정보")
public class GoogleUserInfo {
    @Schema(description = "Google 사용자 ID")
    private String id;

    @Schema(description = "사용자 이메일")
    private String email;

    @Schema(description = "프로필 이미지 URL")
    private String picture;

    @Schema(description = "사용자 이름")
    private String name;
}