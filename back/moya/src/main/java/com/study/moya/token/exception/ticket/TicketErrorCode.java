package com.study.moya.token.exception.ticket;

import com.study.moya.error.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TicketErrorCode implements ErrorCode {
    INSUFFICIENT_TICKET(HttpStatus.BAD_REQUEST, "001", "티켓 잔액이 부족합니다"),
    INSUFFICIENT_ROADMAP_TICKET(HttpStatus.BAD_REQUEST, "002", "로드맵 티켓이 부족합니다"),
    INSUFFICIENT_WORKSHEET_TICKET(HttpStatus.BAD_REQUEST, "003", "워크시트 티켓이 부족합니다"),
    TICKET_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "004", "티켓 계정이 존재하지 않습니다"),
    TICKET_USAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "005", "티켓 사용 내역이 존재하지 않습니다"),
    TICKET_TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "006", "티켓 거래 내역이 존재하지 않습니다"),
    INVALID_TICKET_TYPE(HttpStatus.BAD_REQUEST, "007", "유효하지 않은 티켓 타입입니다"),
    TICKET_ALREADY_USED(HttpStatus.BAD_REQUEST, "008", "이미 사용된 티켓입니다"),
    TICKET_USAGE_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "009", "이미 완료된 티켓 사용입니다"),
    TICKET_USAGE_ALREADY_FAILED(HttpStatus.BAD_REQUEST, "010", "이미 실패 처리된 티켓 사용입니다"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "011", "회원이 존재하지 않습니다"),
    TICKET_TRANSACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "012", "티켓 거래 처리 중 오류가 발생했습니다"),
    TICKET_REFUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "013", "티켓 환불 처리 중 오류가 발생했습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
    private static final String PREFIX = "TICKET";

    @Override
    public String getFullCode() {
        return PREFIX + "_" + code;
    }
}
