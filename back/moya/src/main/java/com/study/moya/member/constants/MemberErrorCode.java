package com.study.moya.member.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode {
    TERMS_NOT_AGREED("필수 이용약관에 동의하지 않았습니다."),
    PRIVACY_POLICY_NOT_AGREED("필수 개인정보 처리방침에 동의하지 않았습니다."),
    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다."),
    MEMBER_NOT_MODIFIABLE("회원 정보를 수정할 수 없습니다. %s"),
    MEMBER_BLOCKED("차단된 회원입니다."),
    MEMBER_WITHDRAWN("탈퇴한 회원입니다."),
    DUPLICATE_NICKNAME("이미 사용 중인 닉네임입니다."),
    MEMBER_NOT_LOGINABLE("로그인 할 수 없는 사용자입니다.");

    private final String messageTemplate;

    public String getMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}