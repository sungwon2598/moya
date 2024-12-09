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
//    // OpenAI API related errors
//    API_CALL_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "001","OpenAI API 호출 중 오류가 발생했습니다"),
//
//    API_RESPONSE_INVALID(HttpStatus.BAD_GATEWAY, "002","OpenAI API 응답이 유효하지 않습니다"),
//
//    // Parsing related errors
//    RESPONSE_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "003","API 응답 파싱 중 오류가 발생했습니다"),
//
//    EMPTY_RESPONSE_ERROR(HttpStatus.BAD_REQUEST, "004","파싱된 결과가 비어있습니다"),
//
//    INVALID_CONTENT_LENGTH(HttpStatus.BAD_REQUEST, "005","컨텐츠 길이가 유효하지 않습니다"),
//
//    // Database related errors
//    SAVE_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "006","로드맵 저장 중 오류가 발생했습니다"),
//
//    // Request validation errors
//    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "007","잘못된 요청입니다"),
//
//    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "008","요청한 주제를 찾을 수 없습니다");
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