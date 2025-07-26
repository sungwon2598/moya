package com.study.moya.admin.dto.roadmap.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "어드민 - 로드맵 기본 정보 수정 요청")
public class UpdateRoadmapInfoRequest {

    @Schema(description = "주제", example = "카산드라 마스터하기")
    private String topic;

    @Min(value = 1, message = "기간은 최소 1주 이상이어야 합니다")
    @Max(value = 52, message = "기간은 최대 52주 이하여야 합니다")
    @Schema(description = "학습 기간(주)", example = "8")
    private Integer duration;

    @Schema(description = "학습 목표", example = "실무에서 활용 가능한 수준")
    private String learningObjective;

    @Schema(description = "커리큘럼 평가", example = "이 로드맵은 초급자에게 적합합니다")
    private String evaluation;
}