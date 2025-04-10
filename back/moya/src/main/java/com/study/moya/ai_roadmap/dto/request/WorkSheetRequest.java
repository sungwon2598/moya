package com.study.moya.ai_roadmap.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record WorkSheetRequest(
        @NotNull
        @Schema(example = "프로그래밍 언어", description = "해당 입력값은 필수입니다.")
        String mainCategory,
        @Schema(example = "자바")
        String subCategory
) {
}
