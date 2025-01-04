package com.study.moya.mypage.exception;

import com.study.moya.error.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MyPageErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "001", "회원이 존재하지 않습니다."),
    MEMBER_BLOCKED(HttpStatus.FORBIDDEN, "002", "차단된 회원입니다."),
    MEMBER_WITHDRAWN(HttpStatus.FORBIDDEN, "003", "탈퇴한 회원입니다."),
    MEMBER_NOT_MODIFIABLE(HttpStatus.BAD_REQUEST, "004", "회원 정보를 수정할 수 없는 상태입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "005", "이미 사용 중인 닉네임입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
    private static final String PREFIX = "MEMBER";

    @Override
    public String getFullCode() {
        return PREFIX + "_" + code;
    }
}