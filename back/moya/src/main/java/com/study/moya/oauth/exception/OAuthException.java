package com.study.moya.oauth.exception;

import com.study.moya.error.exception.BaseException;

public class OAuthException extends BaseException {
    protected OAuthException(OAuthErrorCode errorCode){
        super(errorCode);
    }

    public static OAuthException of(OAuthErrorCode errorCode){
        return new OAuthException(errorCode);
    }
}
