//package com.study.moya.ai_roadmap.exception;
//
//import com.study.moya.error.constants.ErrorCode;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//
//@Getter
//@RequiredArgsConstructor
//public class RoadMapErrorCode implements ErrorCode {
//
//    INVALID_TOKEN(HttpStatus., "001","유효하지 않은 토큰입니다"),
//
//    private final HttpStatus status;
//    private final String code;
//    private final String message;
//    private static final String PREFIX = "AUTH";
//
//    @Override
//    public String getFullCode() {
//        return PREFIX + "_" + code;
//    }
//}