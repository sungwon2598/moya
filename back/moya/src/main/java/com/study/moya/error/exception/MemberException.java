package com.study.moya.error.exception;

import com.study.moya.error.constants.MemberErrorCode;

public class MemberException extends BaseException {
    protected MemberException(MemberErrorCode errorCode) {
        super(errorCode);
    }

    public static MemberException of(MemberErrorCode errorCode) {
        return new MemberException(errorCode);
    }
}