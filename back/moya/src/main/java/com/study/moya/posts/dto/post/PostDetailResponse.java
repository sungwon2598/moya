package com.study.moya.posts.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record PostDetailResponse(
        Long postId,
        String title,
        String content,
        Integer recruits,
        String expectedPeriod,
        @Schema(example = "시작 예정 일자")
        LocalDateTime startDate,
        @Schema(example = "마감 일자")
        LocalDateTime endDate,
        Set<String> studies,
        Set<String> studyDetails,
        String authorName,
        Integer views,
        List<CommentDetail> comments,
        Integer totalComments,
        boolean isLiked
) {
    public record CommentDetail(
         Long commentId,
         String authorName,
         String content,
         LocalDateTime createdAt,
         List<ReplyDetail> replies
    ) {
        public record ReplyDetail(
                Long replyId,
                String replyAuthorName,
                String replyContent,
                LocalDateTime replyCreatedAt
        ) {}
    }
}
