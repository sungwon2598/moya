package com.study.moya.posts.dto.post;

import com.study.moya.posts.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

public record PopularListResponse(
        Long postId,
        String title,
        @Schema(example = "모집 마감 일자")
        String endDate,
        @Schema(example = "대분류")
        Set<String> studies,
        @Schema(example = "중분류")
        Set<String> studyDetails,
        Integer views
) {
    public static PopularListResponse from(Post post) {
        return new PopularListResponse(
                post.getId(),
                post.getTitle(),
                String.valueOf(post.getEndDate()),
                post.getStudies(),
                post.getStudyDetails(),
                post.getViews()
        );

    }
}
