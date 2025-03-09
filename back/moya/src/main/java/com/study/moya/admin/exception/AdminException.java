package com.study.moya.admin.exception;

import com.study.moya.error.exception.BaseException;

public class AdminException extends BaseException {
    protected AdminException(AdminErrorCode errorCode) {
        super(errorCode);
    }

    public static AdminException of(AdminErrorCode errorCode) {
        return new AdminException(errorCode);
    }

    public static AdminException of(AdminErrorCode errorCode, String customMessage) {
        return new AdminException(errorCode) {
            @Override
            public String getMessage() {
                return customMessage;
            }
        };
    }
}
