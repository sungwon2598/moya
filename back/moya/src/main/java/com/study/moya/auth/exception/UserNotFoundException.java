package com.study.moya.auth.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자를 찾을 수 없을 때 발생하는 예외")
public class UserNotFoundException extends RuntimeException {

    public static final String USER_NOT_FOUND = "해당 이메일로 가입된 정보가 없습니다: %s";

    public UserNotFoundException(String email) {
        super(String.format(USER_NOT_FOUND, email));
    }
    public UserNotFoundException(Long id){
        super(String.format(USER_NOT_FOUND, id));
    }
}