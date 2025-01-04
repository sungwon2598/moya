package com.study.moya.member.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberOAuthToken {

    @Column(length = 1000)
    private String accessToken;

    @Column(length = 1000)
    private String refreshToken;

    @Column
    private Instant tokenExpirationTime;

    @Builder
    public MemberOAuthToken(String accessToken, String refreshToken, Instant tokenExpirationTime) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public void updateOAuthToken(String accessToken, String refreshToken, Instant tokenExpirationTime) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpirationTime = tokenExpirationTime;
    }
}