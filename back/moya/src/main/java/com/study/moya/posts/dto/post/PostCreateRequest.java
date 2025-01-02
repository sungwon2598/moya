package com.study.moya.posts.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

public record PostCreateRequest(
        String title,
        String content,
        @Schema(example = "모집 인원")
        Integer recruits,
        String expectedPeriod,
        @Schema(example = "대분류")
        Set<String> studies,
        @Schema(example = "중분류")
        Set<String> studyDetails,
        @Schema(example = "시작 예정 일자")
        LocalDateTime startDate,
        @Schema(example = "모집 마감 일자")
        LocalDateTime endDate
) {
}
