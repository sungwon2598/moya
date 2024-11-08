package com.study.moya.member.domain.history;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HistoryType {
    CREATED("생성"),
    UPDATED("일반 수정"),
    PROFILE_UPDATED("프로필 수정"),
    EMAIL_UPDATED("이메일 수정"),
    PRIVACY_UPDATED("개인정보 동의 변경"),
    POINT_UPDATED("포인트 변경"),
    ACTIVATED("계정 활성화"),
    DORMANT("휴면 전환"),
    BLOCKED("계정 차단"),
    WITHDRAWN("회원 탈퇴");

    private final String description;
}