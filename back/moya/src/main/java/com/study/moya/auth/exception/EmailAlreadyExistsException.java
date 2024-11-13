
package com.study.moya.auth.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미 존재하는 이메일로 가입 시도 시 발생하는 예외")
public class EmailAlreadyExistsException extends RuntimeException {

    public static final String EMAIL_ALREADY_EXISTS = "해당 이메일은 이미 사용중 입니다: %s";

    public EmailAlreadyExistsException(String email) {
        super(String.format(EMAIL_ALREADY_EXISTS, email));
    }
}