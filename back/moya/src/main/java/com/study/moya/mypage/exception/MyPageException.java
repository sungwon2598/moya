package com.study.moya.mypage.exception;

import com.study.moya.error.exception.BaseException;

public class MyPageException extends BaseException {
    protected MyPageException(MyPageErrorCode errorCode) {
        super(errorCode);
    }

    public static MyPageException of(MyPageErrorCode errorCode) {
        return new MyPageException(errorCode);
    }
}
