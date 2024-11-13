package com.study.moya.auth.exception;

public class TokenProcessingException extends RuntimeException {
    public TokenProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}