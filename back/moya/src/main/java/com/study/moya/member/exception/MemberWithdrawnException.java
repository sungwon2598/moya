package com.study.moya.member.exception;

public class MemberWithdrawnException extends RuntimeException {
    public MemberWithdrawnException(String message) {
        super(message);
    }
}