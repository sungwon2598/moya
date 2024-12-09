package com.study.moya.posts.dto.comment;

public record CommentCreateRequest(
        String content,
        Long parentCommentId
) {}
