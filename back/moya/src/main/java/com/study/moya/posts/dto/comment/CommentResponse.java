package com.study.moya.posts.dto.comment;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long commentId,
        String authorName,
        String content,
        LocalDateTime createdAt,
        List<CommentResponse> replies
) {}
