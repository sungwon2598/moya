package com.study.moya.member.exception;

import com.study.moya.member.constants.MemberErrorCode;
import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {
    private final MemberErrorCode errorCode;

    public MemberException(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public MemberException(MemberErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}