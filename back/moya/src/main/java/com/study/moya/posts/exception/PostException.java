package com.study.moya.posts.exception;

import com.study.moya.error.exception.BaseException;

public class PostException extends BaseException {

    protected PostException(PostErrorCode errorCode) {
        super(errorCode);  // BaseException(ErrorCode errorCode) 호출
    }

    public static PostException of(PostErrorCode errorCode) {
        return new PostException(errorCode);
    }
}
