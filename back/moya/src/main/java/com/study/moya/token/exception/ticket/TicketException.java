package com.study.moya.token.exception.ticket;

import com.study.moya.error.exception.BaseException;

public class TicketException extends BaseException {

    protected TicketException(TicketErrorCode errorCode) {
        super(errorCode);
    }

    public static TicketException of(TicketErrorCode errorCode) {
        return new TicketException(errorCode);
    }
}
