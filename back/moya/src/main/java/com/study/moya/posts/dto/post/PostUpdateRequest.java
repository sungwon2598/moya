package com.study.moya.posts.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

public record PostUpdateRequest(
        String title,
        String content,
        Integer recruits,
        String expectedPeriod,
        Set<String> studies,
        Set<String> studyDetails,
        @Schema(example = "시작 예정 일자")
        LocalDateTime startDate,
        @Schema(example = "마감 일자")
        LocalDateTime endDate
) {}