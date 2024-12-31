package com.study.moya.posts.exception;

import com.study.moya.error.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "001", "게시글이 존재하지 않습니다"),
    DELETED_POST(HttpStatus.NOT_FOUND, "002", "삭제된 게시물입니다"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "003", "Member not found"),
    INVALID_AUTHOR(HttpStatus.FORBIDDEN, "004", "해당 글의 작성자가 아닙니다."),
    BLANK_AUTHOR_EMAIL(HttpStatus.BAD_REQUEST, "005", "인증된 사용자가 아닙니다."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "006", "이미 좋아요를 눌렀습니다."),
    NO_LIKED(HttpStatus.BAD_REQUEST, "007", "좋아요를 누른 적이 없습니다."),
    NO_COMMENT(HttpStatus.BAD_REQUEST, "008", "해당 댓글이 존재하지않습니다."),
    CANT_REPLY(HttpStatus.BAD_REQUEST, "009", "대댓글을 생성할 타겟 댓글이 존재하지않습니다."),
    BAD_ACCESS(HttpStatus.BAD_REQUEST, "010", "잘못된 접근입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
    private static final String PREFIX = "POST";

    @Override
    public String getFullCode() {
        return PREFIX + "_" + code;
    }
}
