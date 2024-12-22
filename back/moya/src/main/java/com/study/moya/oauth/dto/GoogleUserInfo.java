package com.study.moya.oauth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfo {
    private String id;
    private String email;
    private String picture;
    private String name;
}
