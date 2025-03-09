package com.study.moya.admin.dto.post;

import com.study.moya.posts.domain.Like;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LikeSummary {
    private Long id;
    private String postTitle;
    private LocalDateTime createdAt;

    public LikeSummary(Like like) {
        this.id = like.getId();
        this.postTitle = like.getPost().getTitle();
        this.createdAt = like.getCreatedAt();
    }
}
