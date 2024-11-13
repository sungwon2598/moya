package com.study.moya.global.exception;

import com.study.moya.member.exception.MemberException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(e.getErrorCode().name(), e.getMessage()));
    }

    @Getter
    @RequiredArgsConstructor
    private static class ErrorResponse {
        private final String code;
        private final String message;
    }
}