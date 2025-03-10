package com.study.moya.admin.dto.post;

import com.study.moya.posts.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentSummary {
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public CommentSummary(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }
}