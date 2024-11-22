package com.study.moya.error.exception;

import com.study.moya.error.constants.AuthErrorCode;

public class AuthException extends BaseException {
    protected AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public static AuthException of(AuthErrorCode errorCode) {
        return new AuthException(errorCode);
    }
}