package com.study.moya.token.exception;

import com.study.moya.error.exception.BaseException;

public class TokenException extends BaseException {

    protected TokenException(TokenErrorCode errorCode) {
        super(errorCode);
    }

    public static TokenException of(TokenErrorCode errorCode) {
        return new TokenException(errorCode);
    }
}