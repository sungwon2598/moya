package com.study.moya.admin.dto.post;

import com.study.moya.posts.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostSummary {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private Boolean isDeleted;

    public PostSummary(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.createdAt = post.getCreatedAt();
        this.isDeleted = post.getIsDeleted();
    }
}