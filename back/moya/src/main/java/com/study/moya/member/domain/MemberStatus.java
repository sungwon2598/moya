package com.study.moya.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
    ACTIVE("활성"),
    DORMANT("휴면"),
    BLOCKED("차단"),
    WITHDRAWN("탈퇴");

    private final String description;
}