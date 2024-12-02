package com.study.moya.chat.error;

import com.study.moya.error.exception.BaseException;

public class ChatException extends BaseException {
    public ChatException(ChatErrorCode errorCode) {
        super(errorCode);
    }

    public static ChatException of(ChatErrorCode errorCode) {
        return new ChatException(errorCode);
    }
}