package com.study.moya.coupon.exception;

import com.study.moya.error.exception.BaseException;

public class CouponException extends BaseException {

    protected CouponException(CouponErrorCode errorCode) {
        super(errorCode);
    }

    public static CouponException of(CouponErrorCode errorCode) {
        return new CouponException(errorCode);
    }
}