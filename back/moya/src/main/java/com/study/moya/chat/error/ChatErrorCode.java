package com.study.moya.chat.error;

import com.study.moya.error.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {
    ALREADY_IN_ROOM(HttpStatus.BAD_REQUEST, "001", "이미 채팅방에 참여 중입니다"),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "002", "채팅방을 찾을 수 없습니다"),
    USER_NOT_IN_ROOM(HttpStatus.BAD_REQUEST, "003", "채팅방에 참여하지 않은 사용자입니다"),
    NOT_ROOM_CREATOR(HttpStatus.FORBIDDEN, "004", "채팅방 생성자가 아닙니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
    private static final String PREFIX = "CHAT";

    @Override
    public String getFullCode() {
        return PREFIX + "_" + code;
    }
}