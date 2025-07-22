package com.study.moya.admin.dto.roadmap.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "어드민 - 주차별 키워드 수정 요청")
public class UpdateWeeklyKeywordRequest {

    @NotBlank(message = "주차별 키워드는 필수입니다")
    @Schema(description = "새로운 주차별 키워드", example = "카산드라 기본 개념 이해")
    private String keyword;
}
