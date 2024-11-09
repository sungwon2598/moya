package com.study.moya.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum MemberStatus {
    ACTIVE("활성화", true),
    DORMANT("휴면", false),
    BLOCKED("차단됨", false),
    WITHDRAWN("탈퇴", false);

    private final String description;
    private final boolean modifiable;

    public String getStateMessage() {
        return String.format("현재 상태: %s(%s)", this.name(), this.description);
    }
}