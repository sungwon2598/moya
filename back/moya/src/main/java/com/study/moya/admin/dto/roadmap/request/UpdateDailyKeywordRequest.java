package com.study.moya.admin.dto.roadmap.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "어드민 - 일별 키워드 수정 요청")
public class UpdateDailyKeywordRequest {

    @NotBlank(message = "일별 키워드는 필수입니다")
    @Schema(description = "새로운 일별 키워드", example = "NoSQL 기본 개념 학습")
    private String keyword;
}
