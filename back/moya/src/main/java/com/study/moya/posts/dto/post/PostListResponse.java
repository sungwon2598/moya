package com.study.moya.posts.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

public record PostListResponse(
        Long postId,
        String title,
        String content,
        @Schema(example = "모집 인원")
        Integer recruits,
        @Schema(example = "스터디 예상 기간")
        String expectedPeriod,
        @Schema(example = "시작 예정 일자")
        LocalDateTime startDate,
        @Schema(example = "모집 마감 일자")
        LocalDateTime endDate,
        @Schema(example = "대분류")
        Set<String> studies,
        @Schema(example = "중분류")
        Set<String> studyDetails,
        String authorName,
        Integer views,
        Integer totalComments,
        boolean isLiked,
        Integer totalLikes
) {
}
